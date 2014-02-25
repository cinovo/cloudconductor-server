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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.util.RPMComparator;
import de.cinovo.cloudconductor.server.web.interfaces.ITemplate;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class TemplatesImpl extends AbstractWebImpl implements ITemplate {
	
	private static final String AUTOREFRESH = "AUTOREFRESH";
	
	
	@Override
	protected String getTemplateFolder() {
		return "templates";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Templates";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return ITemplate.ROOT;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.TEMPLATE;
	}
	
	
	private RPMComparator rpmComp = new RPMComparator();
	private DateTimeFormatter germanFmt = DateTimeFormat.forPattern("HH:mm:ss - dd.MM.yyyy");
	
	
	@Override
	@Transactional
	public ViewModel view() {
		List<ETemplate> etemplates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (ETemplate t : etemplates) {
			Map<String, Object> template = new HashMap<>();
			template.put("name", t.getName());
			template.put("description", t.getDescription());
			template.put("yum", t.getYumPath());
			template.put("autoupdate", t.getAutoUpdate());
			List<Map<String, Object>> rpms = new ArrayList<>();
			for (EPackageVersion rpm : t.getRPMs()) {
				Map<String, Object> rpmmap = new HashMap<>();
				rpmmap.put("name", rpm.getPkg().getName());
				rpmmap.put("version", rpm.getVersion());
				
				EPackage ep = rpm.getPkg();
				List<EPackageVersion> eprpms = new ArrayList<>(ep.getRPMs());
				Collections.sort(eprpms, this.rpmComp);
				if ((rpm.getDeprecated() != null) && rpm.getDeprecated()) {
					rpmmap.put("noexist", true);
				} else if (!eprpms.get(eprpms.size() - 1).equals(rpm)) {
					rpmmap.put("update", true);
				}
				rpms.add(rpmmap);
			}
			Collections.sort(rpms, this.nameMapComp);
			template.put("rpms", rpms);
			
			List<Map<String, Object>> hosts = new ArrayList<>();
			for (EHost host : t.getHosts()) {
				Map<String, Object> hostmap = new HashMap<>();
				hostmap.put("name", host.getName());
				
				DateTime now = new DateTime();
				DateTime dt = new DateTime(host.getLastSeen());
				hostmap.put("lastseen", Minutes.minutesBetween(dt, now).getMinutes() + " Minutes ago  (" + this.germanFmt.print(dt) + ")");
				if (now.minusMinutes(30).getMillis() > host.getLastSeen()) {
					hostmap.put("hoststate", "error");
				} else if (now.minusMinutes(15).getMillis() > host.getLastSeen()) {
					hostmap.put("hoststate", "warning");
				} else {
					hostmap.put("hoststate", "ok");
				}
				
				hosts.add(hostmap);
			}
			Collections.sort(hosts, this.nameMapComp);
			template.put("hosts", hosts);
			
			result.add(template);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView();
		vm.addModel("templates", result);
		vm.addModel("currentPage", ITemplate.ROOT);
		Boolean autorefresh = (Boolean) this.mc.getHttpServletRequest().getSession().getAttribute(TemplatesImpl.AUTOREFRESH);
		vm.addModel(TemplatesImpl.AUTOREFRESH, autorefresh);
		return vm;
	}
	
	@Override
	@Transactional
	public Object changeTemplateState(String tname, String update, String uninstall, String[] updatePackages, String[] deletePackages) {
		RESTAssert.assertNotEmpty(tname);
		if (update != null) {
			return this.updatePackages(tname, updatePackages);
		}
		if (uninstall != null) {
			return this.uninstallPackage(tname, deletePackages);
		}
		this.log("Modified state of template " + tname);
		return this.redirect(null, tname);
	}
	
	private Object uninstallPackage(String tname, String[] pkgs) {
		if ((pkgs == null) || (pkgs.length < 1)) {
			return this.redirect(null, tname);
		}
		ETemplate template = this.dTemplate.findByName(tname);
		Set<EPackageVersion> removedRPMS = new HashSet<>();
		for (String pkg : pkgs) {
			for (EPackageVersion rpm : template.getRPMs()) {
				if (rpm.getPkg().getName().equals(pkg)) {
					template.getRPMs().remove(rpm);
					removedRPMS.add(rpm);
					break;
				}
			}
		}
		template = this.dTemplate.save(template);
		List<EService> services = this.dSvc.findList();
		for (EPackageVersion erpm : removedRPMS) {
			for (EService s : services) {
				if (s.getPackages().contains(erpm.getPkg())) {
					EServiceDefaultState remove = this.dSvcDefState.findByName(s.getName(), template.getName());
					if (remove != null) {
						this.dSvcDefState.delete(remove);
					}
					break;
				}
			}
		}
		
		for (EHost host : template.getHosts()) {
			for (EServiceState service : host.getServices()) {
				for (EPackageVersion erpm : removedRPMS) {
					if (service.getService().getPackages().contains(erpm.getPkg())) {
						this.dSvcState.delete(service);
					}
				}
			}
		}
		return this.redirect(null, tname);
	}
	
	private Object updatePackages(String tname, String[] pkgs) {
		if ((pkgs == null) || (pkgs.length < 1)) {
			return this.redirect(null, tname);
		}
		ETemplate template = this.dTemplate.findByName(tname);
		for (String pkg : pkgs) {
			EPackage ep = this.dPkg.findByName(pkg);
			// delete old version
			for (EPackageVersion installed : template.getRPMs()) {
				if (installed.getPkg().getName().equals(ep.getName())) {
					template.getRPMs().remove(installed);
					break;
				}
			}
			// add newest version
			List<EPackageVersion> rpms = new ArrayList<>(ep.getRPMs());
			Collections.sort(rpms, this.rpmComp);
			template.getRPMs().add(rpms.get(rpms.size() - 1));
		}
		this.dTemplate.save(template);
		return this.redirect(null, tname);
	}
	
	@Override
	@Transactional
	public ViewModel viewAddTemplate() {
		final ViewModel vm = this.createView("addNewTemplate");
		vm.addModel("templatename", "");
		vm.addModel("yumAvailable", this.dPackageServer.findList());
		vm.addModel("yumSelected", "");
		vm.addModel("description", "");
		vm.addModel("autoupdate", false);
		return vm;
	}
	
	@Override
	@Transactional
	public Object addTemplate(String templatename, Long yum, String description, String autoupdate) {
		String error = null;
		if ((templatename == null) || templatename.isEmpty() || templatename.contains(" ")) {
			error = "Please choose a template name!";
		}
		if ((error == null) && (this.dTemplate.findByName(templatename) != null)) {
			error = "The provided template name already exists!";
		}
		if ((error == null) && (yum == null)) {
			error = "Please provide a yum server!";
		}
		
		if (error != null) {
			ViewModel vm = this.viewAddTemplate();
			vm.addModel("newError", error);
			vm.addModel("templatename", templatename);
			vm.addModel("yum", yum);
			vm.addModel("description", description);
			vm.addModel("autoupdate", autoupdate);
			return vm;
		}
		
		ETemplate template = new ETemplate();
		template.setName(templatename);
		template.setDescription(description);
		template.setYum(this.dPackageServer.findById(yum));
		template.setAutoUpdate(Boolean.valueOf(autoupdate));
		this.dTemplate.save(template);
		this.log("Added template " + templatename);
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewEditTemplate(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		
		final ViewModel vm = this.createView("editTemplate");
		vm.addModel("tname", tname);
		vm.addModel("templatename", template.getName());
		vm.addModel("yumAvailable", this.dPackageServer.findList());
		vm.addModel("yumSelected", template.getYumPath());
		vm.addModel("description", template.getDescription());
		vm.addModel("autoupdate", template.getAutoUpdate());
		return vm;
	}
	
	@Override
	@Transactional
	public Object editTemplate(String tname, String templatename, Long yum, String description, String autoupdate) {
		String error = null;
		if ((templatename == null) || templatename.isEmpty() || templatename.contains(" ")) {
			error = "Please choose a template name!";
		}
		if ((error == null) && ((yum == null))) {
			error = "Please provide a yum server!";
		}
		
		if (error != null) {
			ViewModel vm = this.viewEditTemplate(tname);
			vm.addModel("tname", tname);
			vm.addModel("newError", error);
			vm.addModel("templatename", templatename);
			vm.addModel("yum", yum);
			vm.addModel("description", description);
			vm.addModel("autoupdate", autoupdate);
			return vm;
		}
		
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		template.setName(templatename);
		template.setDescription(description);
		template.setYum(this.dPackageServer.findById(yum));
		template.setAutoUpdate(Boolean.valueOf(autoupdate));
		this.dTemplate.save(template);
		this.log("Modified template " + tname);
		return this.redirect(null, tname);
	}
	
	@Override
	@Transactional
	public ViewModel ViewAddPackage(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		List<EPackage> pkgs = this.dPkg.findList();
		List<EPackage> alreadyInstalled = new ArrayList<>();
		for (EPackageVersion rpm : template.getRPMs()) {
			alreadyInstalled.add(rpm.getPkg());
		}
		
		List<String> pkgList = new ArrayList<>();
		for (EPackage p : pkgs) {
			if (!alreadyInstalled.contains(p)) {
				pkgList.add(p.getName());
			}
		}
		
		final ViewModel vm = this.createView("addPackage");
		vm.addModel("templateName", tname);
		vm.addModel("packages", pkgList);
		return vm;
	}
	
	@Override
	@Transactional
	public Object addPackage(String tname, String[] pkgs) {
		RESTAssert.assertNotEmpty(tname);
		if ((pkgs == null) || (pkgs.length < 1)) {
			ViewModel vm = this.ViewAddPackage(tname);
			vm.addModel("chooseError", "Please choose one or more packages!");
			return vm;
		}
		ETemplate template = this.dTemplate.findByName(tname);
		List<EService> services = this.dSvc.findList();
		for (String pkg : pkgs) {
			EPackage ep = this.dPkg.findByName(pkg);
			List<EPackageVersion> rpms = new ArrayList<>(ep.getRPMs());
			Collections.sort(rpms, this.rpmComp);
			EPackageVersion rpm = rpms.get(rpms.size() - 1);
			template.getRPMs().add(rpm);
			for (EService s : services) {
				if (s.getPackages().contains(rpm.getPkg())) {
					EServiceDefaultState sds = this.dSvcDefState.findByName(s.getName(), template.getName());
					if (sds == null) {
						sds = new EServiceDefaultState();
						sds.setService(s);
						sds.setTemplate(template);
						sds.setState(ServiceState.STOPPED);
						this.dSvcDefState.save(sds);
					}
					break;
				}
			}
		}
		// TODO CHECK FOR DEPENDENCYS
		this.dTemplate.save(template);
		this.log("Added pkgs " + this.arrayToString(pkgs) + " to template " + tname);
		return this.redirect(null, tname);
	}
	
	@Override
	@Transactional
	public ViewModel viewDeleteTemplate(String tname) {
		RESTAssert.assertNotEmpty(tname);
		String msg = "Do you really want to remove the template <b>" + tname + "</b>?";
		String header = "Remove template " + tname;
		String back = "#" + tname;
		return this.createDeleteView(header, msg, back, tname);
	}
	
	@Override
	@Transactional
	public Object deleteTemplate(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((template != null) && (template.getHosts().size() < 1)) {
			this.dTemplate.delete(template);
			this.log("Deleted template " + tname);
			return this.redirect(null, tname);
		}
		final ViewModel vm = this.createView("confirmDelete");
		vm.addModel("title", "Remove template");
		vm.addModel("backButton", "/web/templates#" + tname);
		vm.addModel("headline", "Remove template " + tname);
		vm.addModel("descr", "The template <b>" + tname + "</b> can't be removed. There are still hosts alive using this Template");
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewDefaultServiceStates() {
		List<ETemplate> templates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		
		for (ETemplate t : templates) {
			Map<String, Object> host = new HashMap<>();
			host.put("template", t.getName());
			host.put("name", t.getName());
			List<Map<String, Object>> services = new ArrayList<>();
			for (EServiceDefaultState sstate : this.dSvcDefState.findByTemplate(t.getName())) {
				Map<String, Object> service = new HashMap<>();
				service.put("name", sstate.getService().getName());
				service.put("state", sstate.getState());
				services.add(service);
			}
			Collections.sort(services, this.nameMapComp);
			host.put("services", services);
			result.add(host);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView("defaultService");
		vm.addModel("defaultStates", result);
		return vm;
	}
	
	@Override
	@Transactional
	public Response changeDefaultServiceStates(String tname, String[] start, String[] stop, String[] restart) {
		RESTAssert.assertNotEmpty(tname);
		if ((start.length < 1) && (stop.length < 1) && (restart.length < 1)) {
			return this.redirect(ITemplate.DEFAULT_SERVICE_STATE, tname);
		}
		List<EServiceDefaultState> services = this.dSvcDefState.findByTemplate(tname);
		for (String service : start) {
			for (EServiceDefaultState eservice : services) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.RUNNING);
					this.dSvcDefState.save(eservice);
					break;
				}
			}
		}
		for (String service : stop) {
			for (EServiceDefaultState eservice : services) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.STOPPED);
					this.dSvcDefState.save(eservice);
					break;
				}
			}
		}
		for (String service : restart) {
			for (EServiceDefaultState eservice : services) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.RESTARTING);
					this.dSvcDefState.save(eservice);
					break;
				}
			}
		}
		return this.redirect(ITemplate.DEFAULT_SERVICE_STATE, tname);
	}
	
	@Override
	@Transactional
	public Response handleAutorefresh() {
		final HttpSession session = this.mc.getHttpServletRequest().getSession();
		Boolean val = (Boolean) session.getAttribute(TemplatesImpl.AUTOREFRESH);
		if (val == null) {
			session.setAttribute(TemplatesImpl.AUTOREFRESH, true);
		} else {
			session.setAttribute(TemplatesImpl.AUTOREFRESH, !val.booleanValue());
		}
		return this.redirect();
	}
	
}
