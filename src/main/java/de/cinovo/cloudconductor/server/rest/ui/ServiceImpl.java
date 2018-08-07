package de.cinovo.cloudconductor.server.rest.ui;

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IService;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.ServiceHandler;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class ServiceImpl implements IService {
	
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private ITemplateDAO dtemplate;
	
	@Autowired
	private ServiceHandler serviceHandler;
	
	
	@Override
	@Transactional
	public Service[] get() {
		Set<Service> result = new HashSet<>();
		for (EService m : this.serviceDAO.findList()) {
			result.add(m.toApi());
		}
		return result.toArray(new Service[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(Service apiObject) {
		RESTAssert.assertNotNull(apiObject);
		EService model = this.serviceDAO.findByName(apiObject.getName());
		if (model == null) {
			this.serviceHandler.createEntity(apiObject);
		} else {
			this.serviceHandler.updateEntity(model, apiObject);
		}
	}
	
	@Override
	@Transactional
	public Service get(String name) {
		RESTAssert.assertNotEmpty(name);
		EService model = this.serviceDAO.findByName(name);
		RESTAssert.assertNotNull(model, Response.Status.NOT_FOUND);
		return model.toApi();
	}
	
	@Override
	@Transactional
	public Map<String, String> getUsage(String service) {
		RESTAssert.assertNotEmpty(service);
		
		return this.serviceHandler.getServiceUsage(service);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EService model = this.serviceDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		this.serviceDAO.delete(model);
	}
	
}
