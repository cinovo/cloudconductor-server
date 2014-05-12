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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.comparators.PackageVersionComparator;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class TemplateImpl extends ImplHelper implements ITemplate {
	
	@Autowired
	private ITemplateDAO dtemplate;
	@Autowired
	private IPackageVersionDAO drpm;
	@Autowired
	private IHostDAO dhost;
	@Autowired
	private IServiceDAO dsvc;
	@Autowired
	private IServiceStateDAO dsst;
	@Autowired
	private IServiceDefaultStateDAO ddsst;
	@Autowired
	private ISSHKeyDAO dssh;
	@Autowired
	private IFileDAO dcf;
	@Autowired
	private AMConverter amc;
	
	private PackageVersionComparator rpmComp = new PackageVersionComparator();
	
	
	@Override
	@Transactional
	public Template[] get() {
		Set<Template> result = new HashSet<>();
		for (ETemplate m : this.dtemplate.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new Template[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(String name, Template apiObject) {
		this.assertName(name, apiObject);
		ETemplate model = this.amc.toModel(apiObject);
		
		if ((apiObject.getRpms() != null) && !apiObject.getRpms().isEmpty()) {
			List<EPackageVersion> found = new ArrayList<>();
			for (Entry<String, String> s : apiObject.getRpms().entrySet()) {
				EPackageVersion rpm = this.drpm.find(s.getKey(), s.getValue());
				this.assertModelFound(rpm);
				found.add(rpm);
			}
			model.setPackageVersions(found);
		} else {
			model.setPackageVersions(null);
		}
		
		if ((apiObject.getHosts() != null) && !apiObject.getHosts().isEmpty()) {
			model.setHosts(this.findByName(this.dhost, apiObject.getHosts()));
		} else {
			model.setHosts(null);
		}
		
		if ((apiObject.getSshkeys() != null) && !apiObject.getSshkeys().isEmpty()) {
			model.setSshkeys(this.findByName(this.dssh, apiObject.getSshkeys()));
		} else {
			model.setSshkeys(null);
		}
		
		if ((apiObject.getConfigFiles() != null) && !apiObject.getConfigFiles().isEmpty()) {
			model.setConfigFiles(this.findByName(this.dcf, apiObject.getConfigFiles()));
		} else {
			model.setConfigFiles(null);
		}
		this.dtemplate.save(model);
	}
	
	@Override
	@Transactional
	public Template get(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.findByName(this.dtemplate, name);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.dtemplate.findByName(name);
		this.assertModelFound(model);
		this.dtemplate.delete(model);
	}
	
	@Override
	@Transactional
	public Host[] getHosts(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.findByName(this.dtemplate, name);
		Set<Host> result = new HashSet<>();
		for (EHost p : model.getHosts()) {
			result.add(MAConverter.fromModel(p));
		}
		return result.toArray(new Host[result.size()]);
	}
	
	@Override
	@Transactional
	public void addHost(String name, String host) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(host);
		ETemplate model = this.findByName(this.dtemplate, name);
		EHost element = this.findByName(this.dhost, host);
		model.getHosts().add(element);
		this.dtemplate.save(model);
	}
	
	@Override
	@Transactional
	public void removeHost(String name, String host) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(host);
		ETemplate model = this.findByName(this.dtemplate, name);
		EHost element = this.findByName(this.dhost, host);
		model.getHosts().remove(element);
		this.dtemplate.save(model);
	}
	
	@Override
	@Transactional
	public SSHKey[] getSSHKeys(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.findByName(this.dtemplate, name);
		Set<SSHKey> result = new HashSet<>();
		for (ESSHKey p : model.getSshkeys()) {
			result.add(MAConverter.fromModel(p));
		}
		return result.toArray(new SSHKey[result.size()]);
	}
	
	@Override
	@Transactional
	public void addSSHKey(String name, String key) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(key);
		ETemplate model = this.findByName(this.dtemplate, name);
		ESSHKey element = this.findByName(this.dssh, key);
		model.getSshkeys().add(element);
		this.dtemplate.save(model);
	}
	
	@Override
	@Transactional
	public void removeSSHKey(String name, String key) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(key);
		ETemplate model = this.findByName(this.dtemplate, name);
		ESSHKey element = this.findByName(this.dssh, key);
		model.getSshkeys().remove(element);
		this.dtemplate.save(model);
	}
	
	@Override
	@Transactional
	public PackageVersion[] getRPMS(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.findByName(this.dtemplate, name);
		Set<PackageVersion> result = new HashSet<>();
		for (EPackageVersion p : model.getPackageVersions()) {
			result.add(MAConverter.fromModel(p));
		}
		return result.toArray(new PackageVersion[result.size()]);
	}
	
	@Override
	@Transactional
	public void addRPM(String name, PackageVersion rpm) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotNull(rpm);
		ETemplate model = this.findByName(this.dtemplate, name);
		this.assertModelFound(model);
		EPackageVersion erpm = null;
		if ((rpm.getVersion() == null) || rpm.getVersion().isEmpty()) {
			// get newest version
			List<EPackageVersion> rpms = this.drpm.find(rpm.getName());
			Collections.sort(rpms, this.rpmComp);
			erpm = rpms.get(rpms.size() - 1);
		} else {
			// look for version
			erpm = this.drpm.find(rpm.getName(), rpm.getVersion());
		}
		this.assertModelFound(erpm);
		
		boolean exists = false;
		for (EPackageVersion r : model.getPackageVersions()) {
			if (r.getPkg().getName().equals(erpm.getPkg().getName())) {
				if (r.getVersion().equals(erpm.getVersion())) {
					exists = true;
				} else {
					model.getPackageVersions().remove(r);
				}
				break;
			}
		}
		if (!exists) {
			model.getPackageVersions().add(erpm);
			this.dtemplate.save(model);
		}
	}
	
	@Override
	@Transactional
	public void removeRPM(String name, String pkg, String version) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotNull(pkg);
		RESTAssert.assertNotNull(version);
		ETemplate model = this.findByName(this.dtemplate, name);
		EPackageVersion erpm = this.drpm.find(pkg, version);
		this.assertModelFound(erpm);
		
		model.getPackageVersions().remove(erpm);
		model = this.dtemplate.save(model);
		
		for (EHost host : model.getHosts()) {
			for (EServiceState service : host.getServices()) {
				if (service.getService().getPackages().contains(erpm.getPkg())) {
					this.ddsst.delete(this.ddsst.findByName(service.getService().getName(), model.getName()));
					this.dsst.delete(service);
				}
			}
		}
	}
	
	@Override
	@Transactional
	public Service[] getServices(String name) {
		RESTAssert.assertNotEmpty(name);
		ETemplate model = this.findByName(this.dtemplate, name);
		List<EService> services = this.dsvc.findList();
		Set<Service> result = new HashSet<>();
		for (EService s : services) {
			for (EPackageVersion p : model.getPackageVersions()) {
				if (s.getPackages().contains(p.getPkg())) {
					result.add(MAConverter.fromModel(s));
				}
			}
		}
		return result.toArray(new Service[result.size()]);
	}
	
}
