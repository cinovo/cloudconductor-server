package de.cinovo.cloudconductor.server.web2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
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
	
	
	@Override
	protected String getTemplateFolder() {
		return "templates";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerMainMenu(this.getNavElementName(), ITemplate.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + ITemplate.ROOT, this.getNavElementName());
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
	public AjaxRedirect editTemplate(String tname, String templatename, Long packageManagerId, String description, String autoupdate) throws FormErrorException {
		RESTAssert.assertNotEmpty(tname);
		
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
			throw error;
		}
		
		// save the new settings
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		template.setName(templatename);
		template.setDescription(description);
		template.setYum(this.dPackageServer.findById(packageManagerId));
		template.setAutoUpdate(Boolean.valueOf(autoupdate));
		this.dTemplate.save(template);
		this.audit("Modified template " + tname);
		return new AjaxRedirect(IWebPath.WEBROOT + ITemplate.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel addPackageView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		List<EPackage> pkgList = new ArrayList<>();
		for (EPackage p : this.dPkg.findList()) {
			if (!template.getPackageVersions().contains(p)) {
				pkgList.add(p);
			}
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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ViewModel deleteTemplateView(String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		ViewModel modal = this.createModal("deleteTemplate");
		modal.addModel("template", template);
		return modal;
	}
}
