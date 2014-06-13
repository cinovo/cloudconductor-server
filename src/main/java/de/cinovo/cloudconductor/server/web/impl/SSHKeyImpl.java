package de.cinovo.cloudconductor.server.web.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.interfaces.ISSHKey;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class SSHKeyImpl extends AWebPage implements ISSHKey {
	
	@Autowired
	private ISSHKeyDAO dSSH;
	@Autowired
	private ITemplateDAO dTemplate;
	
	
	@Override
	protected String getTemplateFolder() {
		return "ssh";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), ISSHKey.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + ISSHKey.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + ISSHKey.ROOT + IWebPath.ACTION_ADD, "Create new Key");
		this.addViewType("default", "Default", true);
		this.addViewType("template", "by Template", false);
	}
	
	@Override
	protected String getNavElementName() {
		return "SSH Keys";
	}
	
	@Override
	@Transactional
	public RenderedView view(String viewtype) {
		List<ESSHKey> keys = this.dSSH.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		this.sortNamedList(keys);
		
		CSViewModel view;
		if ((viewtype != null) && viewtype.equals("template")) {
			view = this.createView("viewTemplate");
			view.addModel("SIDEBARTYPE", null);
		} else {
			view = this.createView();
		}
		view.addModel("KEYS", keys);
		view.addModel("TEMPLATES", templates);
		return view.render();
	}
	
	@Override
	@Transactional
	public RenderedView addTemplateView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		List<ETemplate> templates = this.dTemplate.findList();
		final CSViewModel model = this.createModal("mAddTemplate");
		model.addModel("KEY", key);
		model.addModel("TEMPLATES", templates);
		return model.render();
	}
	
	@Override
	@Transactional
	public RenderedView addKeyView(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		CSViewModel modal = this.createModal("mAddKey");
		modal.addModel("KEYS", this.dSSH.findList());
		modal.addModel("TEMPLATE", t);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView deleteTemplateView(String owner, String tname) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		CSViewModel modal = this.createModal("mDeleteTemplate");
		modal.addModel("KEY", key);
		modal.addModel("TEMPLATE", template);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView deleteView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		CSViewModel modal = this.createModal("mDeleteKey");
		modal.addModel("KEY", key);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedView editView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		CSViewModel modal = this.prepareView();
		modal.addModel("KEY", key);
		return modal.render();
	}
	
	private CSViewModel prepareView() {
		List<ETemplate> templates = this.dTemplate.findList();
		CSViewModel modal = this.createModal("mModKey");
		modal.addModel("TEMPLATES", templates);
		return modal;
	}
	
	@Override
	@Transactional
	public RenderedView addView() {
		CSViewModel modal = this.prepareView();
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer save(String oldOwner, String owner, String key, String[] templates) throws FormErrorException {
		// Form error handling
		FormErrorException error = null;
		error = this.assertNotEmpty(owner, error, "owner");
		error = this.assertNotEmpty(key, error, "key_content");
		if (error != null) {
			error.addFormParam("owner", owner);
			error.addFormParam("key_content", key);
			error.addFormParam("templates", Arrays.asList(templates));
			if (oldOwner.equals("0")) {
				error.setParentUrl(ISSHKey.ROOT, IWebPath.ACTION_ADD);
			} else {
				error.setParentUrl(ISSHKey.ROOT, oldOwner, IWebPath.ACTION_EDIT);
			}
			throw error;
		}
		ESSHKey ekey = this.dSSH.findByOwner(oldOwner);
		if (ekey == null) {
			ekey = new ESSHKey();
		}
		ekey.setName(owner);
		ekey.setOwner(owner);
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
		this.audit("Modified key " + owner);
		return new AjaxAnswer(IWebPath.WEBROOT + ISSHKey.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer addTemplate(String owner, String[] templates) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		for (String template : templates) {
			ETemplate t = this.dTemplate.findByName(template);
			t.getSshkeys().add(key);
			this.dTemplate.save(t);
		}
		this.audit("Added templates " + this.auditFormat(templates) + " to key " + owner);
		return new AjaxAnswer(IWebPath.WEBROOT + ISSHKey.ROOT, "default");
	}
	
	@Override
	@Transactional
	public AjaxAnswer addKey(String template, String[] keys) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String key : keys) {
			ESSHKey ssh = this.dSSH.findByOwner(key);
			t.getSshkeys().add(ssh);
			this.dTemplate.save(t);
		}
		this.audit("Added keys " + this.auditFormat(keys) + " to template " + template);
		return new AjaxAnswer(IWebPath.WEBROOT + ISSHKey.ROOT, "template");
	}
	
	@Override
	@Transactional
	public AjaxAnswer deleteTemplate(String owner, String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		ESSHKey key = this.dSSH.findByOwner(owner);
		if (template.getSshkeys().contains(key)) {
			template.getSshkeys().remove(key);
			this.dTemplate.save(template);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + ISSHKey.ROOT, "template");
	}
	
	@Override
	@Transactional
	public AjaxAnswer delete(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		this.dSSH.delete(key);
		this.audit("Deleted key " + owner);
		return new AjaxAnswer(IWebPath.WEBROOT + ISSHKey.ROOT);
	}
	
}
