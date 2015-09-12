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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.IService;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.JaxRsComponent;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@JaxRsComponent
public class ServiceImpl extends ImplHelper implements IService {
	
	@Autowired
	private IServiceDAO dservice;
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private AMConverter amc;
	@Autowired
	private IHostDAO dhost;
	@Autowired
	private IServiceStateDAO dSvcState;
	
	
	@Override
	@Transactional
	public Service[] get() {
		Set<Service> result = new HashSet<>();
		for (EService m : this.dservice.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new Service[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(String name, Service apiObject) {
		this.assertName(name, apiObject);
		EService model = this.amc.toModel(apiObject);
		if ((apiObject.getPackages() != null) && !apiObject.getPackages().isEmpty()) {
			model.setPackages(this.findByName(this.dpkg, apiObject.getPackages()));
		} else {
			model.setPackages(null);
		}
		this.dservice.save(model);
	}
	
	@Override
	@Transactional
	public Service get(String name) {
		RESTAssert.assertNotEmpty(name);
		EService model = this.findByName(this.dservice, name);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EService model = this.dservice.findByName(name);
		this.assertModelFound(model);
		this.dservice.delete(model);
	}
	
	@Override
	@Transactional
	public Package[] getPackages(String name) {
		RESTAssert.assertNotEmpty(name);
		EService service = this.findByName(this.dservice, name);
		Set<Package> result = new HashSet<>();
		for (EPackage p : service.getPackages()) {
			result.add(MAConverter.fromModel(p));
		}
		return result.toArray(new Package[result.size()]);
	}
	
	@Override
	@Transactional
	public void addPackage(String name, String pkg) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(pkg);
		EService service = this.findByName(this.dservice, name);
		EPackage p = this.findByName(this.dpkg, pkg);
		service.getPackages().add(p);
		this.dservice.save(service);
	}
	
	@Override
	@Transactional
	public void removePackage(String name, String pkg) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotNull(pkg);
		EService service = this.findByName(this.dservice, name);
		EPackage p = this.findByName(this.dpkg, pkg);
		service.getPackages().remove(p);
		this.dservice.save(service);
	}
	
	@Override
	public void approveServiceStarted(String serviceName, String host) {
		RESTAssert.assertNotEmpty(serviceName);
		RESTAssert.assertNotEmpty(host);
		EHost mHost = this.dhost.findByName(host);
		RESTAssert.assertNotNull(mHost);
		for (EServiceState service : mHost.getServices()) {
			if (!service.getService().getName().equals(serviceName)) {
				continue;
			}
			if (service.getState().equals(ServiceState.STARTED)) {
				service.setState(ServiceState.IN_SERVICE);
				this.dSvcState.save(service);
			}
			break;
		}
	}
	
}
