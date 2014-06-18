package de.cinovo.cloudconductor.server.web.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileDataDAO;
import de.cinovo.cloudconductor.server.dao.IFileTagsDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.EFileTag;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.helper.SidebarType;
import de.cinovo.cloudconductor.server.web.interfaces.IFiles;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
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
	@Autowired
	private IFileTagsDAO dFileTags;
	
	
	@Override
	protected String getTemplateFolder() {
		return "files";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IFiles.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IFiles.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IFiles.ROOT + IWebPath.ACTION_ADD, "Create new File");
		this.addViewType("default", "Default", true);
		this.addViewType("template", "by Template", false);
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
	public RenderedView view(String viewtype, String[] filter) {
		this.clearFilter();
		if ((viewtype != null) && viewtype.equals(IFiles.TEMPLATE_FILTER)) {
			return this.templateView();
		}
		return this.defaultView(filter);
	}
	
	@Transactional
	private RenderedView defaultView(String[] filter) {
		for (EFileTag t : this.dFileTags.findList()) {
			this.addFilter(String.valueOf(t.getId()), t.getName(), false);
		}
		List<EFileTag> tags = new ArrayList<>();
		for (String f : filter) {
			EFileTag tag = this.dFileTags.findById(Long.valueOf(f));
			if (tag != null) {
				tags.add(tag);
			}
		}
		
		List<EFile> daofiles = this.dFile.findList();
		List<EFile> files = new ArrayList<>();
		for (EFile f : daofiles) {
			if (!tags.isEmpty()) {
				if (!Collections.disjoint(f.getTags(), tags)) {
					this.addSidebarElement(f.getName());
					files.add(f);
				}
			} else {
				this.addSidebarElement(f.getName());
				files.add(f);
			}
		}
		this.addSidebarElements(files);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(files);
		CSViewModel view = this.createView();
		view.addModel("FILES", files);
		view.addModel("TEMPLATES", templates);
		return view.render();
	}
	
	@Transactional
	private RenderedView templateView() {
		List<EFile> files = this.dFile.findList();
		for (EFile f : files) {
			this.addSidebarElement(f.getName());
		}
		this.addSidebarElements(files);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(files);
		
		CSViewModel view;
		view = this.createView("viewTemplate");
		view.addModel("SIDEBARTYPE", null);
		view.addModel("FILES", files);
		view.addModel("TEMPLATES", templates);
		return view.render();
	}
	
	@Override
	@Transactional
	public RenderedView newFileView() {
		List<EPackage> packages = this.dPackage.findList();
		this.sortNamedList(packages);
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(templates);
		CSViewModel modal = this.createModal("mModFile");
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView editFileView(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile oldFile = this.dFile.findByName(name);
		EFileData fileData = this.dFileData.findDataByFile(oldFile);
		List<EPackage> packages = this.dPackage.findList();
		List<EService> services = this.dService.findList();
		this.sortNamedList(services);
		this.sortNamedList(packages);
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(templates);
		CSViewModel modal = this.createModal("mModFile");
		modal.addModel("FILE", oldFile);
		modal.addModel("FILEDATA", StringEscapeUtils.escapeHtml(fileData.getData()));
		modal.addModel("PACKAGES", packages);
		modal.addModel("SERVICES", services);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}
	
	@Override
	public RenderedView deleteFileView(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile file = this.dFile.findByName(name);
		CSViewModel modal = this.createModal("mDeleteFile");
		modal.addModel("FILE", file);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView deleteFileFromTemplateView(String name, String template) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		EFile file = this.dFile.findByName(name);
		RESTAssert.assertNotNull(file);
		CSViewModel modal = this.createModal("mDeleteFileFromTemplate");
		modal.addModel("FILE", file);
		modal.addModel("TEMPLATE", t);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView addFileToTemplateView(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		CSViewModel modal = this.createModal("mAddFile");
		List<EFile> files = this.dFile.findList();
		modal.addModel("FILES", files);
		modal.addModel("TEMPLATE", t);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView addTemplateToFileView(String file) {
		RESTAssert.assertNotEmpty(file);
		EFile f = this.dFile.findByName(file);
		RESTAssert.assertNotNull(f);
		CSViewModel modal = this.createModal("mAddTemplate");
		List<ETemplate> templates = this.dTemplate.findList();
		modal.addModel("FILE", f);
		modal.addModel("TEMPLATES", templates);
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer saveFile(String oldname, String newname, String owner, String group, String mode, String targetPath, String content, Boolean isTemplate, String depPackage, String[] depServices, String[] templates) throws FormErrorException {
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
		
		EFileData data = this.dFileData.findDataByFile(cf);
		if (data == null) {
			data = new EFileData();
			data.setParent(cf);
		}
		data.setData(content);
		data = this.dFileData.save(data);
		
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
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT, this.getCurrentViewType());
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteFile(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile cf = this.dFile.findByName(name);
		if (cf == null) {
			return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT);
		}
		EFileData data = this.dFileData.findDataByFile(cf);
		this.dFile.delete(cf);
		this.dFileData.delete(data);
		this.removeSidebarElement(name);
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteFileFromTemplate(String name, String template) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(name);
		ETemplate t = this.dTemplate.findByName(template);
		EFile file = this.dFile.findByName(name);
		if (t.getConfigFiles().contains(file)) {
			t.getConfigFiles().remove(file);
			this.dTemplate.save(t);
		}
		// Fill template with models and return.
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT, IFiles.TEMPLATE_FILTER + "#" + template);
	}
	
	@Override
	@Transactional
	public AjaxAnswer addFileToTemplate(String[] name, String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String file : name) {
			EFile f = this.dFile.findByName(file);
			t.getConfigFiles().add(f);
			this.dTemplate.save(t);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT, IFiles.TEMPLATE_FILTER + "#" + template);
	}
	
	@Override
	@Transactional
	public AjaxAnswer addTemplateToFile(String[] template, String name) {
		RESTAssert.assertNotEmpty(name);
		EFile f = this.dFile.findByName(name);
		for (String temp : template) {
			ETemplate t = this.dTemplate.findByName(temp);
			t.getConfigFiles().add(f);
			this.dTemplate.save(t);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IFiles.ROOT, IFiles.TEMPLATE_FILTER + "#" + template);
	}
	
}
