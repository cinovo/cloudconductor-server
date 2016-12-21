package de.cinovo.cloudconductor.server.web.impl;

import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.helper.SidebarType;
import de.cinovo.cloudconductor.server.web.interfaces.IDirectories;
import de.cinovo.cloudconductor.server.web.interfaces.IDirectories;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.RenderedUI;
import de.taimos.restutils.RESTAssert;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author jawe09
 * 		
 */
public class DirectoriesImpl extends AWebPage implements IDirectories {
	
	@Autowired
	protected IDirectoryDAO dDirectory;
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected IPackageDAO dPackage;
	@Autowired
	private IServiceDAO dService;
	
	
	@Override
	protected String getTemplateFolder() {
		return "directories";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IDirectories.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IDirectories.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IDirectories.ROOT + IWebPath.ACTION_ADD, "Create new Directory");
		this.addViewType("default", "Default", true);
		this.addViewType("template", "by Template", false);
	}
	
	@Override
	protected SidebarType getSidebarType() {
		return SidebarType.ALPHABETICAL;
	}
	
	@Override
	protected String getNavElementName() {
		return "Directories";
	}
	
	@Override
	@Transactional
	public RenderedUI view(String viewtype, String[] filter) {
		this.clearFilter();
		if ((viewtype != null) && viewtype.equals(IDirectories.TEMPLATE_FILTER)) {
			return this.templateView();
		}
		return this.defaultView(filter);
	}
	
	@Transactional
	private RenderedUI defaultView(String[] filter) {

		List<EDirectory> daodirectories = this.dDirectory.findList();
		List<EDirectory> directories = new ArrayList<>();
		for (EDirectory d : daodirectories) {

				this.addSidebarElement(d.getName());
				directories.add(d);

		}
		this.addSidebarElements(directories);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(directories);
		CSViewModel view = this.createView();
		view.addModel("DIRECTORIES", directories);
		view.addModel("TEMPLATES", templates);
		return view.render();
	}
	
	@Transactional
	private RenderedUI templateView() {
		List<EDirectory> directories = this.dDirectory.findList();
		for (EDirectory d : directories) {
			this.addSidebarElement(d.getName());
		}
		this.addSidebarElements(directories);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(directories);
		
		CSViewModel view;
		view = this.createView("viewTemplate");
		view.addModel("SIDEBARTYPE", null);
		view.addModel("DIRECTORIES", directories);
		view.addModel("TEMPLATES", templates);
		return view.render();
	}
	
	@Override
	@Transactional
	public RenderedUI newDirectoryView() {
		List<EPackage> packages = this.dPackage.findList();
		this.sortNamedList(packages);
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(templates);
		CSViewModel modal = this.createModal("mModDirectory");
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedUI editDirectoryView(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory oldDirectory = this.dDirectory.findByName(name);
		List<EPackage> packages = this.dPackage.findList();
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		this.sortNamedList(packages);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(templates);
		CSViewModel modal = this.createModal("mModDirectory");
		modal.addModel("DIRECTORY", oldDirectory);
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}

	@Override
	public RenderedUI deleteDirectoryView(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory directory = this.dDirectory.findByName(name);
		CSViewModel modal = this.createModal("mDeleteDirectory");
		modal.addModel("DIRECTORY", directory);
		return modal.render();
	}

	@Override
	@Transactional
	public RenderedUI deleteDirectoryFromTemplateView(String name, String template) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		EDirectory directory = this.dDirectory.findByName(name);
		RESTAssert.assertNotNull(directory);
		CSViewModel modal = this.createModal("mDeleteDirectoryFromTemplate");
		modal.addModel("DIRECTORY", directory);
		modal.addModel("TEMPLATE", t);
		return modal.render();
	}

	@Override
	@Transactional
	public RenderedUI addDirectoryToTemplateView(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		CSViewModel modal = this.createModal("mAddDirectory");
		List<EDirectory> directories = this.dDirectory.findList();
		modal.addModel("DIRECTORIES", directories);
		modal.addModel("TEMPLATE", t);
		return modal.render();
	}

	@Override
	@Transactional
	public RenderedUI addTemplateToDirectoryView(String directory) {
		RESTAssert.assertNotEmpty(directory);
		EDirectory f = this.dDirectory.findByName(directory);
		RESTAssert.assertNotNull(f);
		CSViewModel modal = this.createModal("mAddTemplate");
		List<ETemplate> templates = this.dTemplate.findList();
		modal.addModel("DIRECTORY", f);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}

	@Override
	@Transactional
	public AjaxAnswer saveDirectory(String oldname, String newname, String owner, String group, String mode, String targetPath, String depPackage, String[] depServices, String[] templates) throws FormErrorException {
		RESTAssert.assertNotEmpty(oldname);
		EDirectory dir = this.dDirectory.findByName(oldname);

		// Form error handling
		FormErrorException error = null;
		error = this.assertNotEmpty(newname, error, "name");
		error = this.assertNotEmpty(owner, error, "owner");
		error = this.assertNotEmpty(group, error, "group");
		error = this.assertNotEmpty(mode, error, "mode");
		error = this.assertNotEmpty(targetPath, error, "targetPath");
		error = this.assertNotEmpty(owner, error, "owner");

		if (!oldname.equals(newname) && (this.dDirectory.findByName(newname) != null)) {
			error = error == null ? this.createError("The service name already exists.") : error;
			error.addElementError("name", true);
		}
		if ((newname != null) && !newname.isEmpty() && newname.contains(" ")) {
			error = error == null ? this.createError("The service name may not contain spaces.") : error;
			error.addElementError("name", true);
		}
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", newname);
			error.addFormParam("owner", owner);
			error.addFormParam("group", group);
			error.addFormParam("mode", mode);
			error.addFormParam("targetPath", targetPath);
			error.addFormParam("depPackage", depPackage);
			error.addFormParam("depServices", Arrays.asList(depServices));
			error.addFormParam("templates", templates);

			if (oldname.equals("0")) {
				error.setParentUrl(IDirectories.ROOT, IWebPath.ACTION_ADD);
			} else {
				error.setParentUrl(IDirectories.ROOT, oldname, IWebPath.ACTION_EDIT);
			}
			throw error;
		}

		// save process
		if (dir == null) {
			dir = new EDirectory();
			dir.setDependentServices(new ArrayList<EService>());
		}
		dir.setName(newname);
		dir.setOwner(owner);
		dir.setGroup(group);
		dir.setFileMode(mode);
		dir.setTargetPath(targetPath);
		dir.setPkg(this.dPackage.findByName(depPackage));
		if (depServices.length > 0) {
			Set<EService> notfound = new HashSet<>(dir.getDependentServices());
			for (String service : depServices) {
				EService eservice = this.dService.findByName(service);
				if (!dir.getDependentServices().contains(eservice)) {
					dir.getDependentServices().add(eservice);
				}
				notfound.remove(eservice);
			}
			dir.getDependentServices().removeAll(notfound);
		} else {
			dir.getDependentServices().clear();
		}
		
		dir = this.dDirectory.save(dir);

		
		List<ETemplate> notfound = this.dTemplate.findList();
		for (String template : templates) {
			ETemplate temp = this.dTemplate.findByName(template);
			if (!temp.getDirectory().contains(dir)) {
				temp.getDirectory().add(dir);
				this.dTemplate.save(temp);
			}
			notfound.remove(temp);
		}
		
		for (ETemplate template : notfound) {
			if (template.getDirectory().contains(dir)) {
				template.getDirectory().remove(dir);
				this.dTemplate.save(template);
			}
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT, this.getCurrentViewType());
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteDirectory(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory dir = this.dDirectory.findByName(name);
		if (dir == null) {
			return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT);
		}

		this.dDirectory.delete(dir);
		this.removeSidebarElement(name);
		return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteDirectoryFromTemplate(String name, String template) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(name);
		ETemplate t = this.dTemplate.findByName(template);
		EDirectory dir = this.dDirectory.findByName(name);

		if (t.getDirectory().contains(dir)) {
			t.getDirectory().remove(dir);
			this.dTemplate.save(t);
		}

		// Fill template with models and return.
		return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT, IDirectories.TEMPLATE_FILTER + "#" + template);
	}
	
	@Override
	@Transactional
	public AjaxAnswer addDirectoryToTemplate(String[] name, String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String directory : name) {
			EDirectory d = this.dDirectory.findByName(directory);
			t.getDirectory().add(d);
			this.dTemplate.save(t);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT, IDirectories.TEMPLATE_FILTER + "#" + template);
	}
	
	@Override
	@Transactional
	public AjaxAnswer addTemplateToDirectory(String[] template, String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory d = this.dDirectory.findByName(name);
		for (String temp : template) {
			ETemplate t = this.dTemplate.findByName(temp);
			t.getDirectory().add(d);
			this.dTemplate.save(t);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IDirectories.ROOT, IDirectories.TEMPLATE_FILTER + "#" + template);
	}
	
}
