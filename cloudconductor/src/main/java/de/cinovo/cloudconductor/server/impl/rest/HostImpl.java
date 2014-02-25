package de.cinovo.cloudconductor.server.impl.rest;

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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.tools.AMConverter;
import de.cinovo.cloudconductor.server.model.tools.MAConverter;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class HostImpl extends ImplHelper implements IHost {
	
	@Autowired
	private IHostDAO dhost;
	@Autowired
	private IServiceStateDAO dss;
	@Autowired
	private IServiceDAO dservice;
	@Autowired
	private IPackageDAO dpkg;
	
	@Autowired
	private AMConverter amc;
	
	
	@Override
	@Transactional
	public Host[] get() {
		Set<Host> result = new HashSet<>();
		for (EHost m : this.dhost.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new Host[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(String name, Host apiObject) {
		this.assertName(name, apiObject);
		EHost model = this.amc.toModel(apiObject);
		
		if ((apiObject.getServices() != null) && !apiObject.getServices().isEmpty()) {
			Set<EServiceState> found = new HashSet<>();
			for (String s : apiObject.getServices()) {
				EServiceState m = this.dss.findByName(s, apiObject.getName());
				if (m != null) {
					found.add(m);
				}
			}
			model.setServices(found);
		} else {
			model.setServices(null);
		}
		model.setLastSeen(new DateTime().getMillis());
		this.dhost.save(model);
	}
	
	@Override
	@Transactional
	public Host get(String name) {
		RESTAssert.assertNotEmpty(name);
		EHost model = this.findByName(this.dhost, name);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EHost model = this.dhost.findByName(name);
		this.assertModelFound(model);
		this.dhost.delete(model);
	}
	
	@Override
	@Transactional
	public Service[] getServices(String name) {
		RESTAssert.assertNotEmpty(name);
		EHost model = this.dhost.findByName(name);
		this.assertModelFound(model);
		Set<Service> result = new HashSet<>();
		for (EServiceState s : model.getServices()) {
			Service svc = MAConverter.fromModel(s);
			result.add(svc);
		}
		return result.toArray(new Service[result.size()]);
	}
	
	@Override
	@Transactional
	public void setService(String host, String name, Service service) {
		RESTAssert.assertNotEmpty(host);
		this.assertName(name, service);
		EHost model = this.findByName(this.dhost, host);
		this.assertModelFound(model);
		
		EService s = this.amc.toModel(service);
		s.setPackages(this.findByName(this.dpkg, service.getPackages()));
		s = this.dservice.save(s);
		
		EServiceState ss = this.dss.findByName(service.getName(), host);
		if (ss == null) {
			ss = new EServiceState();
			ss.setHost(model);
			ss.setService(s);
		}
		ss.setState(service.getState());
		ss = this.dss.save(ss);
		model.getServices().add(ss);
		this.dhost.save(model);
	}
	
	@Override
	@Transactional
	public void removeService(String name, String service) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotNull(service);
		EHost model = this.findByName(this.dhost, name);
		this.assertModelFound(model);
		EServiceState ss = this.dss.findByName(service, name);
		this.assertModelFound(model);
		this.dss.delete(ss);
	}
	
	@Override
	@Transactional
	public Boolean inSync(String host) {
		RESTAssert.assertNotNull(host);
		EHost h = this.findByName(this.dhost, host);
		this.assertModelFound(h);
		Set<EPackageVersion> trpms = h.getTemplate().getRPMs();
		if ((h.getPackages() == null) || h.getPackages().isEmpty()) {
			return false;
		}
		for (EPackageState hpkg : h.getPackages()) {
			if (!trpms.contains(hpkg.getVersion())) {
				return false;
			}
		}
		
		for (EPackageVersion v : trpms) {
			boolean found = false;
			for (EPackageState hpkg : h.getPackages()) {
				if (v.equals(hpkg.getVersion())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	@Transactional
	public Response startService(String host, String service) {
		RESTAssert.assertNotNull(host);
		RESTAssert.assertNotNull(service);
		EHost eHost = this.dhost.findByName(host);
		for (EServiceState s : eHost.getServices()) {
			if (s.getService().getName().equals(service)) {
				s.setState(ServiceState.STARTING);
				this.dss.save(s);
				return Response.ok().build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	@Override
	@Transactional
	public Response stopService(String host, String service) {
		RESTAssert.assertNotNull(host);
		RESTAssert.assertNotNull(service);
		EHost eHost = this.dhost.findByName(host);
		for (EServiceState s : eHost.getServices()) {
			if (s.getService().getName().equals(service)) {
				s.setState(ServiceState.STOPPING);
				this.dss.save(s);
				return Response.ok().build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	@Override
	@Transactional
	public Response restartService(String host, String service) {
		RESTAssert.assertNotNull(host);
		RESTAssert.assertNotNull(service);
		EHost eHost = this.dhost.findByName(host);
		for (EServiceState s : eHost.getServices()) {
			if (s.getService().getName().equals(service)) {
				s.setState(ServiceState.RESTARTING);
				this.dss.save(s);
				return Response.ok().build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
}
