package de.cinovo.cloudconductor.server.web.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.helper.SidebarType;
import de.cinovo.cloudconductor.server.web.interfaces.IServices;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ServiceImpl extends AWebPage implements IServices {
	
	@Autowired
	private IServiceDAO dService;
	@Autowired
	private IPackageDAO dPackage;
	
	
	@Override
	protected String getTemplateFolder() {
		return "services";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IServices.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IServices.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IServices.ROOT + IWebPath.ACTION_ADD, "Create new Service");
	}
	
	@Override
	protected SidebarType getSidebarType() {
		return SidebarType.ALPHABETICAL;
	}
	
	@Override
	protected String getNavElementName() {
		return "Services";
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EService> services = this.dService.findList();
		for (EService svc : services) {
			this.addSidebarElement(svc.getName());
			svc.getPackages().size();
		}
		this.sortNamedList(services);
		ViewModel view = this.createView();
		view.addModel("SERVICES", services);
		return view;
	}
	
	@Override
	public ViewModel newServiceView() {
		ViewModel modal = this.createModal("mModService");
		List<EPackage> packages = this.dPackage.findList();
		this.sortNamedList(packages);
		modal.addModel("PACKAGES", packages);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel editServiceView(String service) {
		RESTAssert.assertNotEmpty(service);
		ViewModel modal = this.createModal("mModService");
		EService svc = this.dService.findByName(service);
		RESTAssert.assertNotNull(svc);
		svc.getPackages().size();
		List<EPackage> packages = this.dPackage.findList();
		this.sortNamedList(packages);
		modal.addModel("SERVICE", svc);
		modal.addModel("PACKAGES", packages);
		return modal;
	}
	
	@Override
	public ViewModel deleteServiceView(String service) {
		RESTAssert.assertNotEmpty(service);
		ViewModel modal = this.createModal("mDeleteService");
		modal.addModel("SERVICE", this.dService.findByName(service));
		return modal;
	}
	
	@Override
	public ViewModel deletePackageView(String service, String pkg) {
		RESTAssert.assertNotEmpty(service);
		RESTAssert.assertNotEmpty(pkg);
		ViewModel modal = this.createModal("mDeletePackage");
		modal.addModel("SERVICE", this.dService.findByName(service));
		modal.addModel("PACKAGE", this.dPackage.findByName(pkg));
		return modal;
	}
	
	@Override
	public ViewModel addPackageView(String service) {
		RESTAssert.assertNotEmpty(service);
		EService svc = this.dService.findByName(service);
		List<EPackage> pkgs = this.dPackage.findNotUsedPackage(svc);
		this.sortNamedList(pkgs);
		ViewModel modal = this.createModal("mAddPackage");
		modal.addModel("SERVICE", svc);
		modal.addModel("PACKAGES", pkgs);
		return modal;
	}
	
	@Override
	public AjaxAnswer deleteService(String service) throws FormErrorException {
		RESTAssert.assertNotEmpty(service);
		EService svc = this.dService.findByName(service);
		this.dService.delete(svc);
		this.audit("Deleted service " + service);
		return new AjaxAnswer(IWebPath.WEBROOT + IServices.ROOT);
	}
	
	@Override
	public AjaxAnswer deletePackage(String service, String pkg) throws FormErrorException {
		EPackage pkgVersion = this.dPackage.findByName(pkg);
		EService svc = this.dService.findByName(service);
		svc.getPackages().remove(pkgVersion);
		this.dService.save(svc);
		this.audit("Removed package " + pkg + " from service " + svc.getName());
		return new AjaxAnswer(IWebPath.WEBROOT + IServices.ROOT);
	}
	
	@Override
	public AjaxAnswer addPackage(String service, String[] pkgs) throws FormErrorException {
		RESTAssert.assertNotEmpty(service);
		if ((pkgs == null) || (pkgs.length < 1)) {
			throw this.createError("Please select at least one package.");
		}
		EService svc = this.dService.findByName(service);
		for (String pkg : pkgs) {
			EPackage ep = this.dPackage.findByName(pkg);
			if (ep != null) {
				svc.getPackages().add(ep);
				this.dService.save(svc);
			}
		}
		this.audit("Added packages " + this.auditFormat(pkgs) + " to service " + service);
		return new AjaxAnswer(IWebPath.WEBROOT + IServices.ROOT);
	}
	
	@Override
	public AjaxAnswer saveService(String service, String newservice, String initscript, String description, String[] pkgs) throws FormErrorException {
		RESTAssert.assertNotNull(service);
		RESTAssert.assertNotEmpty(newservice);
		RESTAssert.assertNotEmpty(initscript);
		RESTAssert.assertNotEmpty(description);
		FormErrorException error = null;
		error = this.assertNotEmpty(newservice, error, "name");
		error = this.assertNotEmpty(initscript, error, "script");
		error = this.assertNotEmpty(initscript, error, description);
		if (!service.equals(newservice) && (this.dService.findByName(newservice) != null)) {
			error = error == null ? this.createError("The service name already exists.") : error;
			error.addElementError("name", true);
		}
		if ((newservice != null) && !newservice.isEmpty() && newservice.contains(" ")) {
			error = error == null ? this.createError("The service name may not contain spaces.") : error;
			error.addElementError("name", true);
		}
		
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", newservice);
			error.addFormParam("script", initscript);
			error.addFormParam("description", description);
			error.addFormParam("pkgs", Arrays.asList(pkgs));
			error.setParentUrl(IServices.ROOT, service, IWebPath.ACTION_EDIT);
			if (service.equals("0")) {
				error.setParentUrl(IServices.ROOT, IWebPath.ACTION_ADD);
			}
			throw error;
		}
		EService svc = this.dService.findByName(service);
		if (svc == null) {
			svc = new EService();
		}
		svc.setName(newservice);
		svc.setInitScript(initscript);
		svc.setDescription(description);
		svc.getPackages().clear();
		if (pkgs != null) {
			for (String pname : pkgs) {
				EPackage pkg = this.dPackage.findByName(pname);
				svc.getPackages().add(pkg);
			}
		}
		svc = this.dService.save(svc);
		this.audit("Modified service " + svc.getName());
		return new AjaxAnswer(IWebPath.WEBROOT + IServices.ROOT);
	}
}
