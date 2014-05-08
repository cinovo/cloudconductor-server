package de.cinovo.cloudconductor.server.web2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.comparators.DefaultStateComparator;
import de.cinovo.cloudconductor.server.web2.comparators.PackageVersionComparator;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.interfaces.ITemplate;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class TemplatesImpl extends AWebPage implements ITemplate {
	
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	private IPackageDAO dPkg;
	@Autowired
	private IServiceDefaultStateDAO dSvcDefState;
	@Autowired
	private IServiceStateDAO dSvcState;
	@Autowired
	private IPackageServerDAO dPackageServer;
	@Autowired
	private IServiceDAO dSvc;
	@Autowired
	private IPackageVersionDAO dPkgVersion;
	
	
	@Override
	protected String getTemplateFolder() {
		return "templates";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerMainMenu(this.getNavElementName(), ITemplate.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + ITemplate.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + ITemplate.ROOT + IWebPath.ACTION_ADD, "Create new Template");
	}
	
	@Override
	protected String getNavElementName() {
		return "Templates";
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<ETemplate> etemplates = this.dTemplate.findList();
		List<String> updates = new ArrayList<>();
		for (ETemplate t : etemplates) {
			this.addSidebarElement(t.getName());
			this.sortNamedList(t.getHosts());
			Collections.sort(t.getPackageVersions(), new PackageVersionComparator());
			t.getYumPath(); // this line is caused by lazy loading
			
			// collect updateable packages
			for (EPackageVersion pv : t.getPackageVersions()) {
				List<EPackageVersion> versionsOfPackage = new ArrayList<>(pv.getPkg().getRPMs());
				Collections.sort(versionsOfPackage, new PackageVersionComparator());
				if (!versionsOfPackage.get(versionsOfPackage.size() - 1).equals(pv)) {
					updates.add(pv.getName());
				}
			}
		}
		this.sortNamedList(etemplates);
		ViewModel view = this.createView();
		view.addModel("TEMPLATES", etemplates);
		view.addModel("UPDATE", updates);
		return view;
	}
	
	@Override
	@Transactional
	public AjaxRedirect updatePackages(String tname, List<String> updatePackages) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((updatePackages == null) || updatePackages.isEmpty() || (template == null)) {
			return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
		}
		
		// delete old versions
		for (EPackageVersion installed : template.getPackageVersions()) {
			if (updatePackages.contains(installed.getPkg().getName())) {
				template.getPackageVersions().remove(installed);
				break;
			}
		}
		
		// add newest version
		for (String pkg : updatePackages) {
			EPackage ep = this.dPkg.findByName(pkg);
			List<EPackageVersion> rpms = new ArrayList<>(ep.getRPMs());
			Collections.sort(rpms, new PackageVersionComparator());
			template.getPackageVersions().add(rpms.get(rpms.size() - 1));
		}
		
		this.dTemplate.save(template);
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxRedirect changeTemplateState(String tname, List<String> deletePackages) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((deletePackages == null) || deletePackages.isEmpty() || (template == null)) {
			return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
		}
		
		// delete packges from template
		for (EPackageVersion installed : template.getPackageVersions()) {
			if (deletePackages.contains(installed.getPkg().getName())) {
				template.getPackageVersions().remove(installed);
				break;
			}
		}
		
		// delete packages from default state
		List<EServiceDefaultState> defaultStates = this.dSvcDefState.findByTemplate(tname);
		for (EServiceDefaultState state : defaultStates) {
			for (EPackage pkg : state.getService().getPackages()) {
				if (deletePackages.contains(pkg.getName())) {
					this.dSvcDefState.delete(state);
					break;
				}
			}
		}
		
		// delete service state from hosts
		for (EHost host : template.getHosts()) {
			for (EServiceState service : host.getServices()) {
				for (EPackage pkg : service.getService().getPackages()) {
					if (deletePackages.contains(pkg.getName())) {
						this.dSvcState.delete(service);
						break;
					}
				}
			}
		}
		
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel editTemplateView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		
		final ViewModel vm = this.createModal("editTemplate");
		vm.addModel("template", template);
		vm.addModel("availablePM", this.dPackageServer.findList());
		return vm;
	}
	
	@Override
	@Transactional
	public AjaxRedirect editTemplate(String tname, String templatename, Long packageManagerId, String description, String autoupdate, String smoothupdate) throws FormErrorException {
		RESTAssert.assertNotEmpty(tname);
		
		this.templateOptionErrorHandling(templatename, packageManagerId, description, autoupdate, smoothupdate);
		
		// save the new settings
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		template.setName(templatename);
		template.setDescription(description);
		template.setYum(this.dPackageServer.findById(packageManagerId));
		template.setAutoUpdate(Boolean.valueOf(autoupdate));
		template.setSmoothUpdate(Boolean.valueOf(smoothupdate));
		this.dTemplate.save(template);
		this.audit("Modified template " + tname);
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel addPackageView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		List<EPackage> pkgList = this.dPkg.findList();
		for (EPackageVersion pv : template.getPackageVersions()) {
			pkgList.remove(pv.getPkg());
		}
		
		this.sortNamedList(pkgList);
		final ViewModel vm = this.createModal("addPackage");
		vm.addModel("template", template);
		vm.addModel("packages", pkgList);
		return vm;
	}
	
	@Override
	@Transactional
	public AjaxRedirect addPackage(String tname, String[] pkgs) throws FormErrorException {
		RESTAssert.assertNotEmpty(tname);
		if ((pkgs == null) || (pkgs.length < 1)) {
			throw this.createError("Please choose one or more packages!");
		}
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		for (String pkg : pkgs) {
			EPackage ep = this.dPkg.findByName(pkg);
			List<EPackageVersion> pvs = new ArrayList<>(ep.getRPMs());
			Collections.sort(pvs, new PackageVersionComparator());
			template.getPackageVersions().add(pvs.get(pvs.size() - 1));
			
			for (EService s : this.dSvc.findByPackage(ep)) {
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
		this.dTemplate.save(template);
		this.audit("Added packages " + this.auditFormat(pkgs) + " to template " + tname);
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel deleteTemplateView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		ViewModel modal = this.createModal("deleteTemplate");
		modal.addModel("template", template);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect deleteTemplate(String tname) throws FormErrorException {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((template == null) || (template.getHosts().size() > 0)) {
			throw this.createError("The template <b>" + tname + "</b> can't be removed. There are still hosts alive using this Template");
		}
		this.dTemplate.delete(template);
		this.audit("Deleted template " + tname);
		this.removeSidebarElement(tname);
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel defaultServiceStatesView(String tname) {
		ETemplate template = this.dTemplate.findByName(tname);
		List<EServiceDefaultState> defaultStates = this.dSvcDefState.findByTemplate(template.getName());
		Collections.sort(defaultStates, new DefaultStateComparator());
		ViewModel modal = this.createModal("defaultServices");
		modal.addModel("template", template);
		modal.addModel("defaultStates", defaultStates);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect changeDefaultServiceStates(String tname, List<String> startService, List<String> stopService) {
		RESTAssert.assertNotEmpty(tname);
		if (startService.isEmpty() && stopService.isEmpty()) {
			return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
		}
		List<EServiceDefaultState> services = this.dSvcDefState.findByTemplate(tname);
		for (EServiceDefaultState eservice : services) {
			if (startService.contains(eservice.getService().getName())) {
				eservice.setState(ServiceState.RUNNING);
				this.dSvcDefState.save(eservice);
			}
		}
		for (EServiceDefaultState eservice : services) {
			if (stopService.contains(eservice.getService().getName())) {
				eservice.setState(ServiceState.STOPPED);
				this.dSvcDefState.save(eservice);
			}
		}
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel addTemplateView() {
		ViewModel modal = this.createModal("addTemplate");
		modal.addModel("availablePM", this.dPackageServer.findList());
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect addTemplate(String templatename, Long packageManagerId, String description, String autoupdate, String smoothupdate) throws FormErrorException {
		this.templateOptionErrorHandling(templatename, packageManagerId, description, autoupdate, smoothupdate);
		
		// save the new settings
		ETemplate template = new ETemplate();
		RESTAssert.assertNotNull(template);
		template.setName(templatename);
		template.setDescription(description);
		template.setYum(this.dPackageServer.findById(packageManagerId));
		template.setAutoUpdate(Boolean.valueOf(autoupdate));
		template.setSmoothUpdate(Boolean.valueOf(smoothupdate));
		this.dTemplate.save(template);
		this.audit("Created new template " + template.getName());
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	private void templateOptionErrorHandling(String templatename, Long packageManagerId, String description, String autoupdate, String smoothupdate) throws FormErrorException {
		String errorMessage = "Please fill in all the information.";
		FormErrorException error = null;
		// templatename and packageManagerId are needed, description and auto update are not
		error = this.checkForEmpty(templatename, errorMessage, error, "templatename");
		error = this.checkForEmpty(packageManagerId.toString(), errorMessage, error, "packageManager");
		if (packageManagerId < 0) {
			if (error == null) {
				error = this.createError(errorMessage);
			}
			error.addElementError("packageManager", true);
		}
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("templatename", templatename);
			error.addFormParam("packageManager", packageManagerId.toString());
			error.addFormParam("description", description);
			error.addFormParam("autoupdate", autoupdate);
			error.addFormParam("smoothupdate", smoothupdate);
			throw error;
		}
	}
}
