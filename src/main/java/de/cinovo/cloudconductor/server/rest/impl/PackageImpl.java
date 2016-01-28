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

import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@JaxRsComponent
public class PackageImpl extends ImplHelper implements IPackage {
	
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private IPackageVersionDAO drpm;
	@Autowired
	private IDependencyDAO ddep;
	@Autowired
	private AMConverter amc;
	
	
	@Override
	@Transactional
	public Package[] get() {
		Set<Package> result = new HashSet<>();
		for (EPackage m : this.dpkg.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new Package[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(Package apiObject) {
		RESTAssert.assertNotNull(apiObject);
		EPackage m = this.amc.toModel(apiObject);
		if ((apiObject.getRpms() != null) && !apiObject.getRpms().isEmpty()) {
			Set<EPackageVersion> found = new HashSet<>();
			for (String s : apiObject.getRpms()) {
				EPackageVersion rpm = this.drpm.find(m.getName(), s);
				this.assertModelFound(rpm);
				found.add(rpm);
			}
			m.setRPMs(found);
		} else {
			m.setRPMs(null);
		}
		this.dpkg.save(m);
	}
	
	@Override
	@Transactional
	public Package get(String name) {
		RESTAssert.assertNotEmpty(name);
		EPackage model = this.findByName(this.dpkg, name);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EPackage model = this.dpkg.findByName(name);
		this.assertModelFound(model);
		this.dpkg.delete(model);
	}
	
	@Override
	@Transactional
	public PackageVersion[] getRPMS(String name) {
		RESTAssert.assertNotEmpty(name);
		Set<PackageVersion> result = new HashSet<>();
		for (EPackageVersion m : this.drpm.find(name)) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new PackageVersion[result.size()]);
	}
	
	@Override
	@Transactional
	public void addRPM(String name, String version, PackageVersion rpm) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotNull(rpm);
		RESTAssert.assertEquals(rpm.getVersion(), version);
		EPackageVersion model = this.amc.toModel(rpm);
		EPackage pkg = this.findByName(this.dpkg, name);
		model.setPkg(pkg);
		
		Set<EDependency> found = new HashSet<>();
		if (rpm.getDependencies() != null) {
			for (Dependency d : rpm.getDependencies()) {
				EDependency md = this.amc.toModel(d);
				md = this.ddep.save(md);
				found.add(md);
			}
		}
		model.setDependencies(found);
		
		model = this.drpm.save(model);
		pkg.getRPMs().add(model);
		this.dpkg.save(pkg);
	}
	
	@Override
	@Transactional
	public void removeRPM(String name, String version) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(version);
		EPackageVersion rpm = this.drpm.find(name, version);
		this.assertModelFound(rpm);
		this.drpm.delete(rpm);
	}
	
}
