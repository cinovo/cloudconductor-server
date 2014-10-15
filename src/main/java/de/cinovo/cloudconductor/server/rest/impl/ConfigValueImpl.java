package de.cinovo.cloudconductor.server.rest.impl;

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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IConfigValue;
import de.cinovo.cloudconductor.api.model.KeyValue;
import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class ConfigValueImpl implements IConfigValue {
	
	private static final String RESERVED_GLOBAL = "GLOBAL";
	
	@Autowired
	private IConfigValueDAO dcv;
	
	
	@Override
	@Transactional
	public Map<String, String> get(String template) {
		RESTAssert.assertNotEmpty(template);
		Map<String, String> result = new HashMap<>();
		for (EConfigValue ecv : this.dcv.findGlobal()) {
			result.put(ecv.getConfigkey(), ecv.getValue());
		}
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			for (EConfigValue ecv : this.dcv.findBy(template)) {
				result.put(ecv.getConfigkey(), ecv.getValue());
			}
		}
		return result;
	}
	
	@Override
	@Transactional
	public Map<String, String> get(String template, String service) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		Map<String, String> result = this.get(template);
		for (EConfigValue ecv : this.dcv.findGlobal(service)) {
			result.put(ecv.getConfigkey(), ecv.getValue());
		}
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			for (EConfigValue ecv : this.dcv.findBy(template, service)) {
				result.put(ecv.getConfigkey(), ecv.getValue());
			}
		}
		return result;
	}
	
	@Override
	@Transactional
	public String get(String template, String service, String key) {
		RESTAssert.assertNotEmpty(template);
		String lService = service;
		String lKey = key;
		if ((key == null) && (service != null)) {
			lKey = service;
			lService = null;
		}
		
		EConfigValue result = null;
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			result = this.dcv.findBy(template, lService, lKey);
			if (result == null) {
				result = this.dcv.findKey(template, lKey);
			}
		}
		if (result == null) {
			result = this.dcv.findGlobal(lService, lKey);
		}
		if (result == null) {
			result = this.dcv.findKey(lKey);
		}
		
		if (result == null) {
			throw new NotFoundException();
		}
		return result.getValue();
	}
	
	@Override
	@Transactional
	public void save(String template, String service, KeyValue apiObject) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		RESTAssert.assertNotEmpty(apiObject.getKey());
		EConfigValue ecv = null;
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			ecv = this.dcv.findBy(template, service, apiObject.getKey());
		} else {
			ecv = this.dcv.findGlobal(service, apiObject.getKey());
		}
		
		if (ecv == null) {
			ecv = new EConfigValue();
			if (template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
				ecv.setTemplate(ConfigValueImpl.RESERVED_GLOBAL);
			} else {
				ecv.setTemplate(template);
			}
			ecv.setService(service);
			ecv.setConfigkey(apiObject.getKey());
		}
		ecv.setValue(apiObject.getValue().toString());
		this.dcv.save(ecv);
	}
	
	@Override
	@Transactional
	public void save(String template, KeyValue apiObject) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(apiObject.getKey());
		EConfigValue ecv = null;
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			ecv = this.dcv.findKey(template, apiObject.getKey());
		} else {
			ecv = this.dcv.findKey(apiObject.getKey());
		}
		
		if (ecv == null) {
			ecv = new EConfigValue();
			if (template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
				ecv.setTemplate(ConfigValueImpl.RESERVED_GLOBAL);
			} else {
				ecv.setTemplate(template);
			}
			ecv.setService(null);
			ecv.setConfigkey(apiObject.getKey());
		}
		ecv.setValue(apiObject.getValue().toString());
		this.dcv.save(ecv);
	}
	
	@Override
	@Transactional
	public void delete(String template, String service, String key) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		RESTAssert.assertNotEmpty(key);
		EConfigValue ecv = null;
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			ecv = this.dcv.findBy(template, service, key);
		} else {
			ecv = this.dcv.findGlobal(service, key);
		}
		if (ecv != null) {
			this.dcv.delete(ecv);
		}
	}
	
	@Override
	@Transactional
	public void delete(String template, String key) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(key);
		EConfigValue ecv = null;
		if (!template.equalsIgnoreCase(ConfigValueImpl.RESERVED_GLOBAL)) {
			ecv = this.dcv.findKey(template, key);
		} else {
			ecv = this.dcv.findKey(key);
		}
		if (ecv != null) {
			this.dcv.delete(ecv);
		}
	}
	
}
