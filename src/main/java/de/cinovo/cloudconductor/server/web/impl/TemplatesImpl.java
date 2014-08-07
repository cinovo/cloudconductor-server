package de.cinovo.cloudconductor.server.web.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.comparators.DefaultStateComparator;
import de.cinovo.cloudconductor.server.comparators.PackageVersionComparator;
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
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.interfaces.ITemplate;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
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
	public RenderedView view() {
		List<ETemplate> etemplates = this.dTemplate.findList();
		List<String> updates = new ArrayList<>();
		for (ETemplate t : etemplates) {
			this.addSidebarElement(t.getName());
			this.sortNamedList(t.getHosts());
			Collections.sort(t.getPackageVersions(), new PackageVersionComparator());
			
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
		CSViewModel view = this.createView();
		view.addModel("TEMPLATES", etemplates);
		view.addModel("UPDATE", updates);
		return view.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer updatePackages(String tname, List<String> updatePackages) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((updatePackages == null) || updatePackages.isEmpty() || (template == null)) {
			return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
		}
		
		// delete old versions
		Set<EPackageVersion> remove = new HashSet<>();
		for (EPackageVersion installed : template.getPackageVersions()) {
			if (updatePackages.contains(installed.getPkg().getName())) {
				remove.add(installed);
			}
		}
		template.getPackageVersions().removeAll(remove);
		
		// add newest version
		for (String pkg : updatePackages) {
			EPackage ep = this.dPkg.findByName(pkg);
			List<EPackageVersion> rpms = new ArrayList<>(ep.getRPMs());
			Collections.sort(rpms, new PackageVersionComparator());
			if (!template.getPackageVersions().contains(rpms.get(rpms.size() - 1))) {
				template.getPackageVersions().add(rpms.get(rpms.size() - 1));
			}
		}
		
		this.dTemplate.save(template);
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer changeTemplateState(String tname, List<String> deletePackages) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((deletePackages == null) || deletePackages.isEmpty() || (template == null)) {
			return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
		}
		
		// delete packges from template
		Set<EPackageVersion> remove = new HashSet<>();
		for (EPackageVersion installed : template.getPackageVersions()) {
			if (deletePackages.contains(installed.getPkg().getName())) {
				remove.add(installed);
			}
		}
		template.getPackageVersions().removeAll(remove);
		
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
		
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public RenderedView editTemplateView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		
		final CSViewModel vm = this.createModal("mEditTemplate");
		vm.addModel("template", template);
		vm.addModel("availablePM", this.dPackageServer.findList());
		return vm.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer editTemplate(String tname, String templatename, Long packageManagerId, String description, String autoupdate, String smoothupdate) throws FormErrorException {
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
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public RenderedView addPackageView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		List<EPackage> pkgList = this.dPkg.findList();
		for (EPackageVersion pv : template.getPackageVersions()) {
			pkgList.remove(pv.getPkg());
		}
		
		this.sortNamedList(pkgList);
		final CSViewModel vm = this.createModal("mAddPackage");
		vm.addModel("template", template);
		vm.addModel("packages", pkgList);
		return vm.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer addPackage(String tname, String[] pkgs) throws FormErrorException {
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
		this.dTemplate.save(template);// , "Added packages " + this.auditFormat(pkgs) + " to template " + tname);
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public RenderedView deleteTemplateView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		CSViewModel modal = this.createModal("mDeleteTemplate");
		modal.addModel("template", template);
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteTemplate(String tname) throws FormErrorException {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		if ((template == null) || (template.getHosts().size() > 0)) {
			throw this.createError("The template <b>" + tname + "</b> can't be removed. There are still hosts alive using this Template");
		}
		this.dTemplate.delete(template);
		this.removeSidebarElement(tname);
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public RenderedView defaultServiceStatesView(String tname) {
		ETemplate template = this.dTemplate.findByName(tname);
		for (EService svc : this.dSvc.findList()) {
			for (EPackageVersion pkv : template.getPackageVersions()) {
				if (svc.getPackages().contains(pkv.getPkg())) {
					this.setDefaultService(svc, template);
				}
			}
		}
		List<EServiceDefaultState> defaultStates = this.dSvcDefState.findByTemplate(template.getName());
		Collections.sort(defaultStates, new DefaultStateComparator());
		CSViewModel modal = this.createModal("mDefaultServices");
		modal.addModel("template", template);
		modal.addModel("defaultStates", defaultStates);
		return modal.render();
	}
	
	private void setDefaultService(EService service, ETemplate template) {
		EServiceDefaultState sds = this.dSvcDefState.findByName(service.getName(), template.getName());
		if (sds == null) {
			sds = new EServiceDefaultState();
			sds.setService(service);
			sds.setTemplate(template);
			sds.setState(ServiceState.STOPPED);
			this.dSvcDefState.save(sds);
		}
	}
	
	@Override
	@Transactional
	public AjaxAnswer changeDefaultServiceStates(String tname, List<String> startService, List<String> stopService) {
		RESTAssert.assertNotEmpty(tname);
		if (startService.isEmpty() && stopService.isEmpty()) {
			return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
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
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public RenderedView addTemplateView() {
		CSViewModel modal = this.createModal("mAddTemplate");
		modal.addModel("availablePM", this.dPackageServer.findList());
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer addTemplate(String templatename, Long packageManagerId, String description, String autoupdate, String smoothupdate) throws FormErrorException {
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
		return new AjaxAnswer(IWebPath.WEBROOT + ITemplate.ROOT);
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
