package de.cinovo.cloudconductor.server.web2.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileDataDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web2.helper.SidebarType;
import de.cinovo.cloudconductor.server.web2.interfaces.IFiles;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

public class FilesImpl extends AWebPage implements IFiles {
	
	@Autowired
	protected IFileDAO dFile;
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected IPackageDAO dPackage;
	@Autowired
	private IServiceDAO dService;
	@Autowired
	private IFileDataDAO dFileData;
	
	
	@Override
	protected String getTemplateFolder() {
		return "files";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IFiles.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IFiles.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IFiles.ROOT + IWebPath.ACTION_ADD, "Create new File");
		this.addFilter("default", "Default", true);
		this.addFilter("template", "by Template", false);
	}
	
	@Override
	protected SidebarType getSidebarType() {
		return SidebarType.ALPHABETICAL;
	}
	
	@Override
	protected String getNavElementName() {
		return "Files";
	}
	
	@Override
	@Transactional
	public ViewModel view(String filter) {
		List<EFile> files = this.dFile.findList();
		for (EFile f : files) {
			this.addSidebarElement(f.getName());
			f.getDependentServices().size(); // lazy loading ...
		}
		this.addSidebarElements(files);
		List<ETemplate> templates = this.dTemplate.findList();
		for (ETemplate t : templates) {
			t.getConfigFiles().size(); // lazy loading ...
		}
		this.sortNamedList(files);
		ViewModel view;
		if ((filter != null) && filter.equals(IFiles.TEMPLATE_FILTER)) {
			view = this.createView("viewTemplate");
			view.addModel("SIDEBARTYPE", null);
		} else {
			view = this.createView();
		}
		view.addModel("FILES", files);
		view.addModel("TEMPLATES", templates);
		return view;
	}
	
	@Override
	@Transactional
	public ViewModel newFileView() {
		List<EPackage> packages = this.dPackage.findList();
		this.sortNamedList(packages);
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(templates);
		ViewModel modal = this.createModal("mModFile");
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel editFileView(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile oldFile = this.dFile.findByName(name);
		for (EService a : oldFile.getDependentServices()) {
			a.getId(); // lazy loading ...
		}
		List<EPackage> packages = this.dPackage.findList();
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		this.sortNamedList(packages);
		List<ETemplate> templates = this.dTemplate.findList();
		for (ETemplate t : templates) {
			t.getConfigFiles().size(); // lazy loading ...
		}
		this.sortNamedList(templates);
		ViewModel modal = this.createModal("mModFile");
		modal.addModel("FILE", oldFile);
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal;
		
	}
	
	@Override
	public ViewModel deleteFileView(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile file = this.dFile.findByName(name);
		ViewModel modal = this.createModal("mDeleteFile");
		modal.addModel("FILE", file);
		return modal;
	}
	
	@Override
	public ViewModel deleteFileFromTemplateView(String name, String template) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		EFile file = this.dFile.findByName(name);
		RESTAssert.assertNotNull(file);
		ViewModel modal = this.createModal("mDeleteFile");
		modal.addModel("FILE", file);
		modal.addModel("TEMPLATE", t);
		return modal;
	}
	
	@Override
	public ViewModel addFileToTemplateView(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		ViewModel modal = this.createModal("mDeleteFile");
		List<EFile> files = this.dFile.findList();
		modal.addModel("FILES", files);
		modal.addModel("TEMPLATE", t);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect saveFile(String oldname, String newname, String owner, String group, String mode, String targetPath, String content, Boolean isTemplate, String depPackage, String[] depServices, String[] templates) throws FormErrorException {
		RESTAssert.assertNotEmpty(oldname);
		EFile cf = this.dFile.findByName(oldname);
		
		// Form error handling
		FormErrorException error = null;
		error = this.assertNotEmpty(newname, error, "name");
		error = this.assertNotEmpty(owner, error, "owner");
		error = this.assertNotEmpty(group, error, "group");
		error = this.assertNotEmpty(mode, error, "mode");
		error = this.assertNotEmpty(targetPath, error, "targetPath");
		error = this.assertNotEmpty(content, error, "file_content");
		error = this.assertNotEmpty(owner, error, "owner");
		
		if (!oldname.equals(newname) && (this.dFile.findByName(newname) != null)) {
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
			error.addFormParam("file_content", content);
			error.addFormParam("isTemplate", isTemplate);
			error.addFormParam("depPackage", depPackage);
			error.addFormParam("depServices", Arrays.asList(depServices));
			error.addFormParam("templates", templates);
			
			if (oldname.equals("0")) {
				error.setParentUrl(IFiles.ROOT, IWebPath.ACTION_ADD);
			} else {
				error.setParentUrl(IFiles.ROOT, oldname, IWebPath.ACTION_EDIT);
			}
			throw error;
		}
		
		// save process
		if (cf == null) {
			cf = new EFile();
			cf.setDependentServices(new ArrayList<EService>());
		}
		cf.setName(newname);
		cf.setOwner(owner);
		cf.setGroup(group);
		cf.setFileMode(mode);
		cf.setTargetPath(targetPath);
		cf.setTemplate(isTemplate == null ? false : true);
		cf.setPkg(this.dPackage.findByName(depPackage));
		if (depServices.length > 0) {
			cf.setReloadable(true);
			Set<EService> notfound = new HashSet<>(cf.getDependentServices());
			for (String service : depServices) {
				EService eservice = this.dService.findByName(service);
				if (!cf.getDependentServices().contains(eservice)) {
					cf.getDependentServices().add(eservice);
				}
				notfound.remove(eservice);
			}
			cf.getDependentServices().removeAll(notfound);
		} else {
			cf.setReloadable(false);
			cf.getDependentServices().clear();
		}
		cf = this.dFile.save(cf);
		EFileData data = cf.getData();
		if (data == null) {
			data = new EFileData();
			data.setParent(cf);
		}
		data.setData(content);
		data = this.dFileData.save(data);
		cf.setData(data);
		
		try {
			byte[] array = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			cf.setChecksum(sb.toString());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// should never happen, if it does-> leave checksum empty
		}
		
		cf = this.dFile.save(cf);
		
		List<ETemplate> notfound = this.dTemplate.findList();
		for (String template : templates) {
			ETemplate temp = this.dTemplate.findByName(template);
			if (!temp.getConfigFiles().contains(cf)) {
				temp.getConfigFiles().add(cf);
				this.dTemplate.save(temp);
			}
			notfound.remove(temp);
		}
		
		for (ETemplate template : notfound) {
			if (template.getConfigFiles().contains(cf)) {
				template.getConfigFiles().remove(cf);
				this.dTemplate.save(template);
			}
		}
		this.audit("Modified file " + cf.getName());
		return new AjaxRedirect(IWebPath.WEBROOT + IFiles.ROOT, this.getCurrentFilter());
	}
	
	@Override
	@Transactional
	public AjaxRedirect deleteFile(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile cf = this.dFile.findByName(name);
		if (cf == null) {
			return new AjaxRedirect(IWebPath.WEBROOT + IFiles.ROOT);
		}
		this.dFile.delete(cf);
		this.removeSidebarElement(name);
		this.audit("Deleted file " + name);
		return new AjaxRedirect(IWebPath.WEBROOT + IFiles.ROOT);
	}
	
	@Override
	public AjaxRedirect deleteFileFromTemplate(String name, String template) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(name);
		ETemplate t = this.dTemplate.findByName(template);
		EFile file = this.dFile.findByName(name);
		if (t.getConfigFiles().contains(file)) {
			t.getConfigFiles().remove(file);
			this.dTemplate.save(t);
		}
		// Fill template with models and return.
		this.audit("Removed file " + name + " from template " + template);
		return new AjaxRedirect(IWebPath.WEBROOT + IFiles.ROOT, IFiles.TEMPLATE_FILTER + "#" + template);
	}
	
	@Override
	public AjaxRedirect addFileToTemplate(String[] name, String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String file : name) {
			EFile f = this.dFile.findByName(file);
			t.getConfigFiles().add(f);
			this.dTemplate.save(t);
		}
		this.audit("Added files " + this.auditFormat(name) + " to template " + template);
		return new AjaxRedirect(IWebPath.WEBROOT + IFiles.ROOT, IFiles.TEMPLATE_FILTER + "#" + template);
	}
	
}
