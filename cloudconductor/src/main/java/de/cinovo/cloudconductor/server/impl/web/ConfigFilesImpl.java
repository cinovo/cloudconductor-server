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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.KeyValue;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.web.interfaces.IFile;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigFilesImpl extends AbstractWebImpl implements IFile {
	
	@Override
	protected String getTemplateFolder() {
		return "files";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Files";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IFile.ROOT;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.FILE;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EFile> files = this.dFile.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (EFile cf : files) {
			Map<String, Object> configfile = new HashMap<>();
			configfile.put("name", cf.getName());
			configfile.put("mode", cf.getFileMode());
			configfile.put("group", cf.getGroup());
			configfile.put("owner", cf.getOwner());
			configfile.put("targetPath", cf.getTargetPath());
			configfile.put("isTemplate", cf.isTemplate());
			configfile.put("isReloadable", cf.isReloadable());
			if (cf.getPkg() != null) {
				configfile.put("depPackage", cf.getPkg().getName());
			} else {
				configfile.put("depPackage", "-");
			}
			
			List<String> services = new ArrayList<>();
			for (EService service : cf.getDependentServices()) {
				services.add(service.getName());
			}
			Collections.sort(services);
			configfile.put("services", services);
			
			List<String> rtemplates = new ArrayList<>();
			for (ETemplate template : templates) {
				if (template.getConfigFiles().contains(cf)) {
					rtemplates.add(template.getName());
				}
			}
			Collections.sort(rtemplates);
			configfile.put("templates", rtemplates);
			
			result.add(configfile);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView();
		vm.addModel("configfiles", result);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewByTemplate() {
		List<EFile> cfs = this.dFile.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (ETemplate t : templates) {
			Map<String, Object> configfile = new HashMap<>();
			configfile.put("name", t.getName());
			ArrayList<Object> files = new ArrayList<>();
			for (EFile cf : cfs) {
				if (t.getConfigFiles().contains(cf)) {
					Map<String, Object> fileInfo = new HashMap<>();
					fileInfo.put("name", cf.getName());
					fileInfo.put("isTemplate", cf.isTemplate());
					StringBuilder restarts = new StringBuilder();
					for (EService s : cf.getDependentServices()) {
						restarts.append(s.getName());
						restarts.append(";");
					}
					fileInfo.put("restarts", restarts.toString());
					fileInfo.put("package", cf.getPkg() == null ? "-" : cf.getPkg().getName());
					files.add(fileInfo);
				}
			}
			configfile.put("files", files);
			result.add(configfile);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView("viewByTemplate");
		vm.addModel("templates", result);
		return vm;
	}
	
	@Override
	@Transactional
	public Object deleteFile(String cfname) {
		RESTAssert.assertNotEmpty(cfname);
		EFile cf = this.dFile.findByName(cfname);
		if (cf == null) {
			return this.redirect();
		}
		// this.dFileData.delete(cf.getData());
		this.dFile.delete(cf);
		this.log("Deleted file " + cfname);
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewAddFile() {
		final ViewModel vm = this.getDefaultValues(null);
		vm.addModel("addMode", true);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewEditFile(String cfname) {
		RESTAssert.assertNotEmpty(cfname);
		EFile cf = this.dFile.findByName(cfname);
		Map<String, Object> result = new HashMap<>();
		result.put("name", cf.getName());
		result.put("owner", cf.getOwner());
		result.put("group", cf.getGroup());
		result.put("mode", cf.getFileMode());
		result.put("targetPath", cf.getTargetPath());
		result.put("isTemplate", cf.isTemplate());
		result.put("depPackage", cf.getPkg());
		if (cf.getData() != null) {
			result.put("content", StringEscapeUtils.escapeHtml(cf.getData().getData()));
		}
		
		final ViewModel vm = this.getDefaultValues(cf);
		vm.addModel("cname", cf.getName());
		vm.addModel("file", result);
		vm.addModel("addMode", false);
		return vm;
	}
	
	@Override
	@Transactional
	public Object saveFile(String cfname, String name, String owner, String group, String mode, String targetPath, String content, Boolean isTemplate, String depPackage, String[] depServices, String[] templates) {
		EFile cf = this.dFile.findByName(cfname);
		// form check
		String error = null;
		if ((!cfname.equals(name)) && (this.dFile.findByName(name) != null)) {
			error = "This name already exists";
		}
		
		if ((error == null) && name.isEmpty()) {
			error = "Please give a name.";
		} else if (name.contains(" ")) {
			error = "Please use a name without spaces.";
		}
		
		if ((error == null) && owner.isEmpty()) {
			error = "Please give an owner.";
		}
		if ((error == null) && group.isEmpty()) {
			error = "Please give a group.";
		}
		if ((error == null) && mode.isEmpty()) {
			error = "Please give a mode.";
		}
		if ((error == null) && targetPath.isEmpty()) {
			error = "Please give a target path.";
		}
		if ((error == null) && content.isEmpty()) {
			error = "Please give a file content.";
		}
		
		if (error != null) {
			// TODO: USE EXCEPTION MAPPER
			Map<String, Object> result = new HashMap<>();
			result.put("name", name);
			result.put("owner", owner);
			result.put("group", group);
			result.put("mode", mode);
			result.put("targetPath", targetPath);
			result.put("isTemplate", isTemplate == null ? null : true);
			result.put("depPackage", depPackage);
			result.put("content", content);
			
			final ViewModel vm = this.getDefaultValues(cf);
			vm.addModel("cname", cfname);
			vm.addModel("file", result);
			vm.addModel("addMode", cf == null);
			vm.addModel("newError", error);
			return vm;
		}
		
		// save process
		
		if (cf == null) {
			cf = new EFile();
			cf.setDependentServices(new HashSet<EService>());
		}
		
		cf.setName(name);
		cf.setOwner(owner);
		cf.setGroup(group);
		cf.setFileMode(mode);
		cf.setTargetPath(targetPath);
		cf.setTemplate(isTemplate == null ? false : true);
		cf.setPkg(this.dPkg.findByName(depPackage));
		if (depServices.length > 0) {
			cf.setReloadable(true);
			Set<EService> notfound = new HashSet<>(cf.getDependentServices());
			for (String service : depServices) {
				EService eservice = this.dSvc.findByName(service);
				cf.getDependentServices().add(eservice);
				notfound.remove(eservice);
			}
			cf.getDependentServices().removeAll(notfound);
		} else {
			cf.setReloadable(false);
			cf.getDependentServices().clear();
		}
		
		// cf.setData(this.dFileData.save(cf.getData()));
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
		this.log("Modified file " + cf.getName());
		return this.redirect(null, name);
		
	}
	
	private ViewModel getDefaultValues(EFile cf) {
		List<EService> eservices = this.dSvc.findList();
		List<KeyValue> services = new ArrayList<>();
		for (EService service : eservices) {
			if ((cf == null) || !cf.getDependentServices().contains(service)) {
				services.add(new KeyValue(service.getName(), false));
			} else {
				services.add(new KeyValue(service.getName(), true));
			}
		}
		Collections.sort(services);
		
		List<ETemplate> templates = this.dTemplate.findList();
		List<KeyValue> rtemplates = new ArrayList<>();
		for (ETemplate template : templates) {
			if ((cf == null) || !template.getConfigFiles().contains(cf)) {
				rtemplates.add(new KeyValue(template.getName(), false));
			} else {
				rtemplates.add(new KeyValue(template.getName(), true));
			}
		}
		Collections.sort(rtemplates);
		
		List<EPackage> rpkgs = this.dPkg.findList();
		List<KeyValue> pkgs = new ArrayList<>();
		for (EPackage pkg : rpkgs) {
			if ((cf == null) || (cf.getPkg() == null) || !cf.getPkg().equals(pkg)) {
				pkgs.add(new KeyValue(pkg.getName(), false));
			} else {
				pkgs.add(new KeyValue(pkg.getName(), true));
			}
		}
		Collections.sort(pkgs);
		
		final ViewModel vm = this.createView("edit");
		vm.addModel("services", services);
		vm.addModel("templates", rtemplates);
		vm.addModel("packages", pkgs);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAddFileToTemplate(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		ArrayList<Object> result = new ArrayList<>();
		for (EFile file : this.dFile.findList()) {
			if (!t.getConfigFiles().contains(file)) {
				Map<String, Object> fileInfo = new HashMap<>();
				fileInfo.put("name", file.getName());
				fileInfo.put("isTemplate", file.isTemplate());
				StringBuilder restarts = new StringBuilder();
				for (EService s : file.getDependentServices()) {
					restarts.append(s.getName());
					restarts.append(";");
				}
				fileInfo.put("restarts", restarts.toString());
				fileInfo.put("package", file.getPkg() == null ? "-" : file.getPkg().getName());
				result.add(fileInfo);
			}
		}
		
		final ViewModel vm = this.createView("addFile");
		vm.addModel("template", t.getName());
		vm.addModel("files", result);
		return vm;
	}
	
	@Override
	@Transactional
	public Object addFileToTemplate(String template, String[] files) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String file : files) {
			EFile f = this.dFile.findByName(file);
			t.getConfigFiles().add(f);
			this.dTemplate.save(t);
		}
		this.log("Added files " + this.arrayToString(files) + " to template " + template);
		return this.redirect(IFile.VIEW_TEMPLATE, template);
	}
	
	@Override
	@Transactional
	public ViewModel viewRemoveFileFromTemplate(String template, String fname) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(fname);
		String msg = "Do you really want to remove the file " + fname + " from the template <b>" + template + "</b>?";
		String header = "Remove file " + fname + " from template " + template;
		String back = IFile.VIEW_TEMPLATE + "#" + template;
		return this.createDeleteView(header, msg, back, template, fname);
	}
	
	@Override
	@Transactional
	public Response removeFileFromTemplate(String template, String fname) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(fname);
		ETemplate t = this.dTemplate.findByName(template);
		EFile file = this.dFile.findByName(fname);
		if (t.getConfigFiles().contains(file)) {
			t.getConfigFiles().remove(file);
			this.dTemplate.save(t);
		}
		// Fill template with models and return.
		this.log("Removed file " + fname + " from template " + template);
		return this.redirect(IFile.VIEW_TEMPLATE, template);
	}
	
}
