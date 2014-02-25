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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.KeyValue;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.web.interfaces.ISSHKey;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */

public class SSHKeyImpl extends AbstractWebImpl implements ISSHKey {
	
	@Override
	protected String getTemplateFolder() {
		return "ssh";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "SSHKeys";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return ISSHKey.ROOT;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.SSHKEY;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<ESSHKey> keys = this.dSSH.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (ESSHKey key : keys) {
			Map<String, Object> keymap = new HashMap<>();
			keymap.put("name", key.getOwner());
			keymap.put("key", key.getKeycontent());
			List<String> templateList = new ArrayList<>();
			for (ETemplate t : templates) {
				if (t.getSshkeys().contains(key)) {
					templateList.add(t.getName());
				}
			}
			keymap.put("templates", templateList);
			
			result.add(keymap);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView();
		vm.addModel("keys", result);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewByTemplate() {
		List<ESSHKey> keys = this.dSSH.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (ETemplate template : templates) {
			Map<String, Object> templateMap = new HashMap<>();
			templateMap.put("name", template.getName());
			List<String> keyList = new ArrayList<>();
			for (ESSHKey k : keys) {
				if (template.getSshkeys().contains(k)) {
					keyList.add(k.getOwner());
				}
			}
			templateMap.put("keys", keyList);
			
			result.add(templateMap);
		}
		final ViewModel vm = this.createView("viewTemplate");
		vm.addModel("templates", result);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAddTemplate(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		List<ETemplate> templates = this.dTemplate.findList();
		ArrayList<String> result = new ArrayList<String>();
		for (ETemplate t : templates) {
			if (!t.getSshkeys().contains(key)) {
				result.add(t.getName());
			}
		}
		final ViewModel vm = this.createView("addTemplate");
		vm.addModel("templates", result);
		vm.addModel("owner", key.getOwner());
		return vm;
	}
	
	@Override
	@Transactional
	public Object addTemplate(String owner, String[] templates) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		for (String template : templates) {
			ETemplate t = this.dTemplate.findByName(template);
			t.getSshkeys().add(key);
			this.dTemplate.save(t);
		}
		this.log("Added templates " + this.arrayToString(templates) + " to key " + owner);
		return this.redirect(null, owner);
	}
	
	@Override
	@Transactional
	public ViewModel viewAddKey(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		List<ESSHKey> keys = this.dSSH.findList();
		ArrayList<String> result = new ArrayList<String>();
		for (ESSHKey key : keys) {
			if (!t.getSshkeys().contains(key)) {
				result.add(key.getOwner());
			}
		}
		final ViewModel vm = this.createView("addKey");
		vm.addModel("template", t.getName());
		vm.addModel("keys", result);
		return vm;
	}
	
	@Override
	@Transactional
	public Object addKey(String template, String[] keys) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String key : keys) {
			ESSHKey ssh = this.dSSH.findByOwner(key);
			t.getSshkeys().add(ssh);
			this.dTemplate.save(t);
		}
		this.log("Added keys " + this.arrayToString(keys) + " to template " + template);
		return this.redirect(ISSHKey.VIEW_TEMPLATE, template);
	}
	
	@Override
	@Transactional
	public ViewModel viewRemoveTemplate(String owner, String tname) {
		RESTAssert.assertNotEmpty(tname);
		String msg = "Do you really want to remove the ssh key of " + owner + " from the template <b>" + tname + "</b>?";
		String header = "Remove ssh key from template " + tname;
		String back = "#" + owner;
		return this.createDeleteView(header, msg, back, true, owner, "template", tname);
	}
	
	@Override
	@Transactional
	public Response removeTemplate(String owner, String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		ESSHKey key = this.dSSH.findByOwner(owner);
		if (template.getSshkeys().contains(key)) {
			template.getSshkeys().remove(key);
			this.dTemplate.save(template);
		}
		return this.redirect(null, owner);
	}
	
	@Override
	@Transactional
	public ViewModel viewDelete(String owner) {
		RESTAssert.assertNotEmpty(owner);
		String msg = "Do you really want to remove the ssh key of " + owner + "?";
		String header = "Remove ssh key of " + owner;
		String back = "#" + owner;
		return this.createDeleteView(header, msg, back, owner);
	}
	
	@Override
	@Transactional
	public Response delete(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		this.dSSH.delete(key);
		this.log("Deleted key " + owner);
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel edit(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		List<ETemplate> templates = this.dTemplate.findList();
		final ViewModel vm = this.createView("save");
		vm.addModel("owner", key.getOwner());
		vm.addModel("key", key.getKeycontent());
		List<KeyValue> templateList = new ArrayList<>();
		for (ETemplate t : templates) {
			if (t.getSshkeys().contains(key)) {
				templateList.add(new KeyValue(t.getName(), true));
			} else {
				templateList.add(new KeyValue(t.getName(), false));
			}
		}
		Collections.sort(templateList);
		vm.addModel("templates", templateList);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel add() {
		List<ETemplate> templates = this.dTemplate.findList();
		final ViewModel vm = this.createView("save");
		vm.addModel("owner", "");
		vm.addModel("key", "");
		List<KeyValue> templateList = new ArrayList<>();
		for (ETemplate t : templates) {
			templateList.add(new KeyValue(t.getName(), false));
		}
		Collections.sort(templateList);
		vm.addModel("templates", templateList);
		return vm;
	}
	
	@Override
	@Transactional
	public Response save(String fowner, String key, String[] templates) {
		return this.save(null, fowner, key, templates);
	}
	
	@Override
	@Transactional
	public Response save(String owner, String fowner, String key, String[] templates) {
		RESTAssert.assertNotEmpty(fowner);
		ESSHKey ekey = this.dSSH.findByOwner(owner);
		if (ekey == null) {
			ekey = new ESSHKey();
		}
		ekey.setName(fowner);
		ekey.setOwner(fowner);
		ekey.setKeycontent(key);
		ekey = this.dSSH.save(ekey);
		
		List<ETemplate> etemplates = this.dTemplate.findList();
		List<String> tls = Arrays.asList(templates);
		for (ETemplate template : etemplates) {
			if (tls.contains(template.getName())) {
				if (!template.getSshkeys().contains(ekey)) {
					template.getSshkeys().add(ekey);
					this.dTemplate.save(template);
				}
			} else if (template.getSshkeys().contains(ekey)) {
				template.getSshkeys().remove(ekey);
				this.dTemplate.save(template);
			}
		}
		this.log("Modified key " + owner);
		return this.redirect(null, ekey.getOwner());
	}
}
