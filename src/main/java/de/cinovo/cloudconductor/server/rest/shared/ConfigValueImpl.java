package de.cinovo.cloudconductor.server.rest.shared;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import de.cinovo.cloudconductor.api.interfaces.IConfigValue;
import de.cinovo.cloudconductor.api.model.ConfigDiff;
import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.dao.hibernate.ConfigValueDAOHib;
import de.cinovo.cloudconductor.server.handler.ConfigValueHandler;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.cinovo.cloudconductor.server.util.ReservedConfigKeyStore;
import de.cinovo.cloudconductor.server.util.comparators.ConfigValueDiffer;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class ConfigValueImpl implements IConfigValue {

	@Autowired
	private IConfigValueDAO configValueDAO;

	@Autowired
	private ConfigValueHandler handler;

	@Autowired
	private ConfigValueDiffer differ;

	@Override
	public String[] getAvailableTemplates() {
		return this.configValueDAO.findTemplates().toArray(new String[0]);
	}

	@Override
	@Transactional
	public ConfigValue[] get(String template) {
		RESTAssert.assertNotEmpty(template);
		return this.handler.get(template);
	}

	@Override
	public ConfigValue[] getClean(String template) {
		RESTAssert.assertNotEmpty(template);
		return this.handler.getClean(template);
	}

	@Override
	public ConfigValue[] getCleanUnstacked(String template) {
		RESTAssert.assertNotEmpty(template);
		return this.handler.getCleanUnstacked(template);
	}

	@Override
	@Transactional
	public ConfigValue[] get(String template, String service) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		return this.handler.get(template, service);
	}

	@Override
	@Transactional
	public ConfigValue[] getClean(String template, String service) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		return this.handler.getClean(template, service);
	}

	@Override
	@Transactional
	public String get(String template, String service, String key) {
		return this.handler.get(template, service, key);
	}


	@Override
	@Transactional
	public String getClean(String template, String service, String key) {
		RESTAssert.assertNotEmpty(template);
		return this.handler.getClean(template, service, key);
	}

	@Override
	public ConfigValue[] getCleanVars(String template) {
		if(template == null || template.isEmpty() || template.equals("null")) {
			return this.getCleanUnstacked(ConfigValueDAOHib.RESERVED_VARIABLE);
		}
		List<ConfigValue> result = new ArrayList<>();
		for(EConfigValue ecv : this.configValueDAO.findBy(template, ConfigValueDAOHib.RESERVED_VARIABLE)) {
			result.add(ecv.toApi());
		}
		return result.toArray(new ConfigValue[0]);
	}

	@Override
	public ConfigDiff[] diffTemplates(String templateA, String templateB) {
		return this.differ.compare(templateA, templateB);
	}

	@Override
	@Transactional
	public String getExact(String template, String service, String key) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(key);

		EConfigValue result;
		if(ReservedConfigKeyStore.instance.isReserved(key)) {
			return ReservedConfigKeyStore.instance.getValue(key);
		}
		if((service == null) || service.isEmpty() || service.equals("null")) {
			service = null;
		}
		result = this.configValueDAO.findBy(template, service, key);
		if(result == null) {
			throw new NotFoundException();
		}
		return result.getValue();
	}

	@Override
	@Transactional
	public void save(ConfigValue apiObject) {
		RESTAssert.assertNotNull(apiObject);
		RESTAssert.assertNotEmpty(apiObject.getKey());

		if(ReservedConfigKeyStore.instance.isReserved(apiObject.getKey())) {
			throw new NotAcceptableException();
		}
		if((apiObject.getTemplate() == null) || apiObject.getTemplate().isEmpty()) {
			apiObject.setTemplate(ConfigValueDAOHib.RESERVED_GLOBAL);
		}
		if((apiObject.getService() == null) || apiObject.getService().isEmpty()) {
			apiObject.setService(null);
		}
		EConfigValue ecv = this.configValueDAO.findBy(apiObject.getTemplate(), apiObject.getService(), apiObject.getKey());
		if(ecv == null) {
			ecv = new EConfigValue();
			ecv.setTemplate(apiObject.getTemplate());
			ecv.setService(apiObject.getService());
			ecv.setConfigkey(apiObject.getKey());
		}
		ecv.setValue(apiObject.getValue().toString());
		this.configValueDAO.save(ecv);
	}

	@Override
	@Transactional
	public void delete(String template, String service, String key) {
		RESTAssert.assertNotNull(template);
		RESTAssert.assertNotEmpty(key);

		if((template == null) || template.isEmpty()) {
			template = ConfigValueDAOHib.RESERVED_GLOBAL;
		}
		if((service == null) || service.isEmpty() || service.equals("null")) {
			service = null;
		}
		EConfigValue ecv = this.configValueDAO.findBy(template, service, key);
		if(ecv != null) {
			this.configValueDAO.deleteById(ecv.getId());
		}
	}

	@Override
	@Transactional
	public void deleteForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);

		List<EConfigValue> configs = this.configValueDAO.findAll(templateName);

		configs.forEach(cv -> this.configValueDAO.delete(cv));
	}

	@Override
	public ConfigValue[] getUnstacked(String template) {
		return this.getCleanUnstacked(template);
	}

	@Override
	public void deleteForService(String template, String service) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);

		this.configValueDAO.findBy(template, service).forEach(cv -> this.configValueDAO.delete(cv));
	}


}
