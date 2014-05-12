package de.cinovo.cloudconductor.server.web.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.comparators.PackageComparator;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.helper.SidebarType;
import de.cinovo.cloudconductor.server.web.interfaces.IPackages;
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
public class PackagesImpl extends AWebPage implements IPackages {
	
	@Autowired
	protected IPackageDAO dPkg;
	@Autowired
	protected IServiceDAO dSvc;
	@Autowired
	protected IPackageVersionDAO dVersion;
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	private IServiceDefaultStateDAO dSvcDefState;
	
	
	@Override
	protected String getTemplateFolder() {
		return "packages";
	}
	
	@Override
	protected SidebarType getSidebarType() {
		return SidebarType.ALPHABETICAL;
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IPackages.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IPackages.ROOT, "Packages");
	}
	
	@Override
	protected String getNavElementName() {
		return "Packages";
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EPackage> packageList = this.dPkg.findList();
		Collections.sort(packageList, new PackageComparator());
		
		List<EService> serviceList = this.dSvc.findList();
		Multimap<String, EService> serviceMap = ArrayListMultimap.create();
		Multimap<String, EPackageVersion> versionMap = ArrayListMultimap.create();
		
		for (EPackage pkg : packageList) {
			this.addSidebarElement(pkg.getName());
			for (EPackageVersion version : pkg.getRPMs()) {
				versionMap.put(pkg.getName(), version);
			}
			for (EService s : serviceList) {
				if (s.getPackages().contains(pkg)) {
					serviceMap.put(pkg.getName(), s);
				}
			}
		}
		
		ViewModel view = this.createView();
		view.addModel("PACKAGES", packageList);
		view.addModel("SERVICES", serviceMap);
		view.addModel("VERSIONS", versionMap);
		return view;
	}
	
	@Override
	@Transactional
	public ViewModel addPackageView(String pname, Long versionid) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotNull(versionid);
		EPackageVersion version = this.dVersion.findById(versionid);
		List<ETemplate> templates = this.dTemplate.findList();
		List<ETemplate> ts = new ArrayList<>();
		for (ETemplate temp : templates) {
			if (temp.getPackageVersions().contains(version)) {
				continue;
			}
			ts.add(temp);
		}
		this.sortNamedList(ts);
		version.getPkg().getName();// needed caused by lazy loading
		// Fill template with models and return.
		final ViewModel vm = this.createModal("mInstallPackage");
		vm.addModel("templates", ts);
		vm.addModel("version", version);
		return vm;
	}
	
	@Override
	@Transactional
	public AjaxAnswer addPackage(String pname, Long versionId, String[] templates) throws FormErrorException {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotNull(versionId);
		if ((templates == null) || (templates.length < 1)) {
			throw this.createError("Please select at least one template.");
		}
		
		EPackageVersion pv = this.dVersion.findById(versionId);
		List<EService> services = this.dSvc.findList();
		for (String temp : templates) {
			ETemplate t = this.dTemplate.findByName(temp);
			if (t.getPackageVersions() == null) {
				t.setPackageVersions(new ArrayList<EPackageVersion>());
			}
			// check if package exists remove old rpm
			for (EPackageVersion existing : t.getPackageVersions()) {
				if (existing.getPkg().equals(pv.getPkg())) {
					t.getPackageVersions().remove(existing);
					break;
				}
			}
			t.getPackageVersions().add(pv);
			this.dTemplate.save(t);
			
			// update default service list
			for (EService s : services) {
				if (s.getPackages().contains(pv.getPkg())) {
					this.setDefaultService(s, t);
					break;
				}
			}
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IPackages.ROOT);
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
	public ViewModel addServiceView(String pname) {
		RESTAssert.assertNotEmpty(pname);
		EPackage pkg = this.dPkg.findByName(pname);
		List<EService> serviceList = new ArrayList<>();
		for (EService s : this.dSvc.findList()) {
			if (!s.getPackages().contains(pkg)) {
				serviceList.add(s);
			}
		}
		// Fill template with models and return.
		this.sortNamedList(serviceList);
		final ViewModel modal = this.createModal("mAddService");
		modal.addModel("services", serviceList);
		modal.addModel("packageName", pname);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxAnswer addService(String pname, String[] services) throws FormErrorException {
		RESTAssert.assertNotEmpty(pname);
		if ((services == null) || (services.length < 1)) {
			throw this.createError("Please select at least one service.");
		}
		EPackage pkg = this.dPkg.findByName(pname);
		for (String service : services) {
			EService eservice = this.dSvc.findByName(service);
			eservice.getPackages().add(pkg);
			this.dSvc.save(eservice);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IPackages.ROOT + "#" + pname);
	}
	
	@Override
	@Transactional
	public ViewModel newServiceView(String pname) {
		ViewModel modal = this.createModal("mNewService");
		modal.addModel("packageName", pname);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxAnswer newService(String pname, String servicename, String initscript, String description) throws FormErrorException {
		RESTAssert.assertNotEmpty(pname);
		
		String errorMessage = "Please fill in all the information.";
		FormErrorException error = null;
		// servicename and initscript are needed, description not!
		error = this.checkForEmpty(servicename, errorMessage, error, "servicename");
		error = this.checkForEmpty(initscript, errorMessage, error, "initscript");
		
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("servicename", servicename);
			error.addFormParam("initscript", initscript);
			error.addFormParam("description", description);
			throw error;
		}
		
		EPackage pkg = this.dPkg.findByName(pname);
		EService service = new EService();
		service.setName(servicename);
		service.setInitScript(initscript);
		service.setDescription(description);
		service.getPackages().add(pkg);
		service = this.dSvc.save(service);
		
		return new AjaxAnswer(IWebPath.WEBROOT + IPackages.ROOT + "#" + pname);
	}
	
	@Override
	@Transactional
	public ViewModel deleteServiceView(String pname, String sname) {
		ViewModel modal = this.createModal("mDeleteService");
		modal.addModel("packageName", pname);
		modal.addModel("serviceName", sname);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteService(String pname, String sname) {
		RESTAssert.assertNotEmpty(pname);
		RESTAssert.assertNotEmpty(sname);
		EPackage pkgVersion = this.dPkg.findByName(pname);
		EService service = this.dSvc.findByName(sname);
		service.getPackages().remove(pkgVersion);
		this.dSvc.save(service);
		return new AjaxAnswer(IWebPath.WEBROOT + IPackages.ROOT + "#" + pname);
	}
	
}
