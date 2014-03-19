package de.cinovo.cloudconductor.server.impl.web;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.PackageComparator;
import de.cinovo.cloudconductor.server.web.interfaces.IPackages;
import de.cinovo.cloudconductor.server.web2.comparators.PackageVersionComparator;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class PackagesImpl extends AbstractWebImpl implements IPackages {
	
	@Override
	protected String getTemplateFolder() {
		return "packages";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Packages";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IPackages.ROOT;
	}
	
	/**
	 * Return a page showing all packages, their versions, and their services.
	 * 
	 * @return the page showing all packages
	 */
	@Override
	@Transactional
	public ViewModel view() {
		// Build packages model.
		List<EPackage> packages = this.dPkg.findList();
		Collections.sort(packages, new PackageComparator());
		List<Object> packagesModel = new ArrayList<>();
		List<EService> services = this.dSvc.findList();
		
		for (EPackage pkg : packages) {
			// Build versions model.
			List<EPackageVersion> rpms = new ArrayList<>(pkg.getRPMs());
			Collections.sort(rpms, new PackageVersionComparator());
			
			List<Object> versionsModel = new ArrayList<>();
			for (EPackageVersion version : rpms) {
				if ((version.getDeprecated() != null) && version.getDeprecated()) {
					continue;
				}
				Map<String, Object> versionModel = new HashMap<>();
				versionModel.put("version", version.getVersion());
				versionModel.put("id", version.getId());
				versionsModel.add(versionModel);
			}
			
			List<Object> serviceModel = new ArrayList<>();
			for (EService s : services) {
				if (s.getPackages().contains(pkg)) {
					Map<String, Object> serviceList = new HashMap<>();
					serviceList.put("name", s.getName());
					serviceList.put("id", s.getId());
					serviceModel.add(serviceList);
				}
			}
			// Build package model.
			Map<String, Object> packageModel = new HashMap<>();
			packageModel.put("name", pkg.getName());
			packageModel.put("services", serviceModel);
			packageModel.put("versions", versionsModel);
			
			// Add package model to packages model.
			packagesModel.add(packageModel);
		}
		
		// Fill template with models and return.
		final ViewModel vm = this.createView();
		vm.addModel("packages", packagesModel);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAddService(String pname) {
		RESTAssert.assertNotEmpty(pname);
		EPackage pkg = this.dPkg.findByName(pname);
		List<EService> services = this.dSvc.findList();
		List<String> serviceList = new ArrayList<>();
		for (EService s : services) {
			if (!s.getPackages().contains(pkg)) {
				serviceList.add(s.getName());
			}
		}
		// Fill template with models and return.
		final ViewModel vm = this.createView("addService");
		vm.addModel("services", serviceList);
		vm.addModel("packageName", pname);
		vm.addModel("servicename", "");
		vm.addModel("initscript", "");
		vm.addModel("description", "");
		return vm;
	}
	
	@Override
	@Transactional
	public Object addService(String pname, String[] services) {
		RESTAssert.assertNotEmpty(pname);
		if ((services == null) || (services.length < 1)) {
			ViewModel vm = this.viewAddService(pname);
			vm.addModel("chooseError", "Please choose a service!");
			return vm;
		}
		EPackage pkg = this.dPkg.findByName(pname);
		for (String service : services) {
			EService eservice = this.dSvc.findByName(service);
			eservice.getPackages().add(pkg);
			this.dSvc.save(eservice);
		}
		// Fill template with models and return.
		return this.redirect(null, pname);
	}
	
	@Override
	@Transactional
	public Object addService(String pname, String servicename, String initscript, String description) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotEmpty(servicename);
		RESTAssert.assertNotEmpty(initscript);
		RESTAssert.assertNotEmpty(description);
		if ((servicename == null) || servicename.isEmpty() || servicename.contains(" ")) {
			ViewModel vm = this.viewAddService(pname);
			vm.addModel("newError", "Please provide a valid service name!");
			vm.addModel("servicename", servicename);
			vm.addModel("initscript", initscript);
			vm.addModel("description", description);
			return vm;
		}
		if (this.dSvc.findByName(servicename) != null) {
			ViewModel vm = this.viewAddService(pname);
			vm.addModel("newError", "The provided service name already exists!");
			vm.addModel("servicename", servicename);
			vm.addModel("initscript", initscript);
			vm.addModel("description", description);
			return vm;
		}
		if ((initscript == null) || initscript.isEmpty()) {
			ViewModel vm = this.viewAddService(pname);
			vm.addModel("newError", "Please provide a initscript!");
			vm.addModel("servicename", servicename);
			vm.addModel("initscript", initscript);
			vm.addModel("description", description);
			return vm;
		}
		EPackage pkg = this.dPkg.findByName(pname);
		
		EService service = new EService();
		service.setName(servicename);
		service.setInitScript(initscript);
		service.setDescription(description);
		service.getPackages().add(pkg);
		service = this.dSvc.save(service);
		return this.redirect(null, pname);
	}
	
	@Override
	@Transactional
	public ViewModel viewDeleteService(String pname, String sname) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotEmpty(sname);
		String msg = "Do you really want to remove the service <b>" + sname + "</b> from package " + pname + "?";
		String header = "Remove service " + sname + " from package " + pname;
		String back = "#" + pname;
		return this.createDeleteView(header, msg, back, true, pname, "services", sname);
	}
	
	@Override
	@Transactional
	public Response deleteService(String pname, String sname) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotEmpty(sname);
		EPackage pkgVersion = this.dPkg.findByName(pname);
		EService service = this.dSvc.findByName(sname);
		service.getPackages().remove(pkgVersion);
		this.dSvc.save(service);
		// Fill template with models and return.
		return this.redirect(null, pname);
	}
	
	@Override
	@Transactional
	public ViewModel viewAddPackage(String pname, Long rpmid) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotNull(rpmid);
		EPackageVersion rpm = this.dVersion.findById(rpmid);
		List<ETemplate> templates = this.dTemplate.findList();
		List<String> ts = new ArrayList<>();
		for (ETemplate temp : templates) {
			if (temp.getPackageVersions().contains(rpm)) {
				continue;
			}
			ts.add(temp.getName());
		}
		// Fill template with models and return.
		final ViewModel vm = this.createView("installPackage");
		vm.addModel("templates", ts);
		vm.addModel("packagename", rpm.getPkg().getName());
		vm.addModel("rpmid", rpm.getId());
		return vm;
	}
	
	@Override
	@Transactional
	public Response addPackage(String pname, Long rpmid, String[] templates) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotNull(rpmid);
		if (templates.length > 0) {
			EPackageVersion rpm = this.dVersion.findById(rpmid);
			List<EService> services = this.dSvc.findList();
			for (String temp : templates) {
				ETemplate t = this.dTemplate.findByName(temp);
				if (t.getPackageVersions() == null) {
					t.setPackageVersions(new ArrayList<EPackageVersion>());
				}
				// check if package exists remove old rpm
				for (EPackageVersion existing : t.getPackageVersions()) {
					if (existing.getPkg().equals(rpm.getPkg())) {
						t.getPackageVersions().remove(existing);
						break;
					}
				}
				t.getPackageVersions().add(rpm);
				this.dTemplate.save(t);
				// update default list
				for (EService s : services) {
					if (s.getPackages().contains(rpm.getPkg())) {
						EServiceDefaultState sds = this.dSvcDefState.findByName(s.getName(), t.getName());
						if (sds == null) {
							sds = new EServiceDefaultState();
							sds.setService(s);
							sds.setTemplate(t);
							sds.setState(ServiceState.STOPPED);
							this.dSvcDefState.save(sds);
						}
						break;
					}
				}
			}
		}
		return this.redirect(null, pname);
	}
}
