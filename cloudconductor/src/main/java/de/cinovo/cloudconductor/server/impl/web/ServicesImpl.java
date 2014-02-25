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

import de.cinovo.cloudconductor.api.model.KeyValue;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.web.interfaces.IService;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ServicesImpl extends AbstractWebImpl implements IService {
	
	@Override
	protected String getTemplateFolder() {
		return "services";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Services";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IService.ROOT;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.SERVICE;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EService> services = this.dSvc.findList();
		List<Map<String, Object>> servicesModel = new ArrayList<>();
		
		for (EService svc : services) {
			// Build versions model.
			Map<String, Object> svxModel = new HashMap<>();
			svxModel.put("name", svc.getName());
			svxModel.put("initscript", svc.getInitScript());
			svxModel.put("descr", svc.getDescription());
			
			List<String> pkgModel = new ArrayList<>();
			for (EPackage pkg : svc.getPackages()) {
				pkgModel.add(pkg.getName());
			}
			svxModel.put("pkgs", pkgModel);
			servicesModel.add(svxModel);
		}
		Collections.sort(servicesModel, this.nameMapComp);
		final ViewModel vm = this.createView();
		vm.addModel("services", servicesModel);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd() {
		List<EPackage> pkgs = this.dPkg.findList();
		List<String> pkgList = new ArrayList<>();
		for (EPackage p : pkgs) {
			pkgList.add(p.getName());
		}
		final ViewModel vm = this.createView("addService");
		vm.addModel("packages", pkgList);
		vm.addModel("servicename", "");
		vm.addModel("initscript", "");
		vm.addModel("description", "");
		return vm;
	}
	
	@Override
	@Transactional
	public Object add(String servicename, String initscript, String description, String[] pkgs) {
		String error = null;
		if ((servicename == null) || servicename.isEmpty() || servicename.contains(" ")) {
			error = "Please choose a servicename!";
		}
		if ((error == null) && (this.dSvc.findByName(servicename) != null)) {
			error = "The provided service name already exists!";
		}
		if ((error == null) && ((initscript == null) || initscript.isEmpty())) {
			error = "Please provide a initscript!";
		}
		
		if (error != null) {
			ViewModel vm = this.viewAdd();
			vm.addModel("newError", error);
			vm.addModel("servicename", servicename);
			vm.addModel("initscript", initscript);
			vm.addModel("description", description);
			return vm;
		}
		EService service = new EService();
		service.setName(servicename);
		service.setInitScript(initscript);
		service.setDescription(description);
		if (pkgs != null) {
			for (String pname : pkgs) {
				EPackage pkg = this.dPkg.findByName(pname);
				service.getPackages().add(pkg);
			}
		}
		service = this.dSvc.save(service);
		this.log("Added service " + service.getName());
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewEdit(String sname) {
		RESTAssert.assertNotEmpty(sname);
		EService service = this.dSvc.findByName(sname);
		RESTAssert.assertNotNull(service);
		
		List<EPackage> pkgs = this.dPkg.findList();
		List<KeyValue> pkgMap = new ArrayList<>();
		for (EPackage p : pkgs) {
			if (service.getPackages().contains(p)) {
				pkgMap.add(new KeyValue(p.getName(), "checked"));
			} else {
				pkgMap.add(new KeyValue(p.getName(), ""));
			}
		}
		final ViewModel vm = this.createView("editService");
		vm.addModel("packages", pkgMap);
		vm.addModel("servicename", service.getName());
		vm.addModel("initscript", service.getInitScript());
		vm.addModel("description", service.getDescription());
		return vm;
	}
	
	@Override
	@Transactional
	public Object edit(String sname, String servicename, String initscript, String description, String[] pkgs) {
		RESTAssert.assertNotEmpty(servicename);
		RESTAssert.assertNotEmpty(initscript);
		RESTAssert.assertNotEmpty(description);
		String error = null;
		if ((servicename == null) || servicename.isEmpty() || servicename.contains(" ")) {
			error = "Please choose a servicename!";
		}
		if ((error == null) && (this.dSvc.findByName(servicename) != null)) {
			error = "The provided service name already exists!";
		}
		if ((error == null) && ((initscript == null) || initscript.isEmpty())) {
			error = "Please provide a initscript!";
		}
		
		if (error != null) {
			ViewModel vm = this.viewEdit(sname);
			vm.addModel("newError", error);
			vm.addModel("servicename", servicename);
			vm.addModel("initscript", initscript);
			vm.addModel("description", description);
			return vm;
		}
		EService service = this.dSvc.findByName(sname);
		service.setName(servicename);
		service.setInitScript(initscript);
		service.setDescription(description);
		service.getPackages().clear();
		if (pkgs != null) {
			for (String pname : pkgs) {
				EPackage pkg = this.dPkg.findByName(pname);
				service.getPackages().add(pkg);
			}
		}
		service = this.dSvc.save(service);
		this.log("Modified service " + service.getName());
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewAddPkg(String sname) {
		RESTAssert.assertNotEmpty(sname);
		EService service = this.dSvc.findByName(sname);
		List<EPackage> pkgs = this.dPkg.findList();
		List<String> pkgList = new ArrayList<>();
		for (EPackage p : pkgs) {
			if (!service.getPackages().contains(p)) {
				pkgList.add(p.getName());
			}
		}
		final ViewModel vm = this.createView("addPackage");
		vm.addModel("packages", pkgList);
		vm.addModel("serviceName", sname);
		return vm;
	}
	
	@Override
	@Transactional
	public Object addPkg(String sname, String[] pkgs) {
		RESTAssert.assertNotEmpty(sname);
		if ((pkgs == null) || (pkgs.length < 1)) {
			ViewModel vm = this.viewAddPkg(sname);
			vm.addModel("chooseError", "Please choose a package!");
			return vm;
		}
		EService service = this.dSvc.findByName(sname);
		for (String pkg : pkgs) {
			EPackage ep = this.dPkg.findByName(pkg);
			service.getPackages().add(ep);
			this.dSvc.save(service);
		}
		this.log("Added packages " + this.arrayToString(pkgs) + " to service " + service.getName());
		return this.redirect(null, sname);
	}
	
	@Override
	@Transactional
	public ViewModel viewRemovePkg(String sname, String pname) {
		RESTAssert.assertNotEmpty(sname);
		RESTAssert.assertNotEmpty(pname);
		String msg = "Do you really want to remove the package <b>" + pname + "</b> from service <b>" + sname + "</b>?";
		String header = "Remove service " + sname + " from package " + pname;
		String back = "#" + sname;
		return this.createDeleteView(header, msg, back, true, sname, "package", pname);
	}
	
	@Override
	@Transactional
	public Response removePkg(String sname, String pname) {
		EPackage pkgVersion = this.dPkg.findByName(pname);
		EService service = this.dSvc.findByName(sname);
		service.getPackages().remove(pkgVersion);
		this.dSvc.save(service);
		this.log("Removed package " + pname + " from service " + service.getName());
		return this.redirect(null, sname);
	}
	
	@Override
	@Transactional
	public ViewModel viewDelete(String sname) {
		RESTAssert.assertNotEmpty(sname);
		String msg = "Do you really want to remove the service <b>" + sname + "</b>?";
		String header = "Remove service " + sname;
		String back = "#" + sname;
		return this.createDeleteView(header, msg, back, sname);
	}
	
	@Override
	@Transactional
	public Response delete(String sname) {
		RESTAssert.assertNotEmpty(sname);
		EService service = this.dSvc.findByName(sname);
		this.dSvc.delete(service);
		this.log("Deleted service " + sname);
		return this.redirect(null, sname);
	}
}
