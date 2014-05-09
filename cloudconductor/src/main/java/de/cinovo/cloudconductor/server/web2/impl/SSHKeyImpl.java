package de.cinovo.cloudconductor.server.web2.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web2.interfaces.ISSHKey;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

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
		this.addFilter("default", "Default", true);
		this.addFilter("template", "by Template", false);
	}
	
	@Override
	protected String getNavElementName() {
		return "SSH Keys";
	}
	
	@Override
	@Transactional
	public ViewModel view(String filter) {
		List<ESSHKey> keys = this.dSSH.findList();
		List<ETemplate> templates = this.dTemplate.findList();
		for (ETemplate t : templates) {
			t.getSshkeys().size(); // lazy loading ...
		}
		this.sortNamedList(keys);
		
		ViewModel view;
		if ((filter != null) && filter.equals("template")) {
			view = this.createView("viewTemplate");
			view.addModel("SIDEBARTYPE", null);
		} else {
			view = this.createView();
		}
		view.addModel("KEYS", keys);
		view.addModel("TEMPLATES", templates);
		return view;
	}
	
	@Override
	@Transactional
	public ViewModel addTemplateView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		List<ETemplate> templates = this.dTemplate.findList();
		for (ETemplate t : templates) {
			t.getSshkeys().size(); // lazy loading ...
		}
		final ViewModel model = this.createModal("mAddTemplate");
		model.addModel("KEY", key);
		model.addModel("TEMPLATES", templates);
		return model;
	}
	
	@Override
	@Transactional
	public ViewModel addKeyView(String template) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		RESTAssert.assertNotNull(t);
		ViewModel modal = this.createModal("mAddKey");
		modal.addModel("KEYS", this.dSSH.findList());
		modal.addModel("TEMPLATE", t);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel deleteTemplateView(String owner, String tname) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		ETemplate template = this.dTemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		ViewModel modal = this.createModal("mDeleteTemplate");
		modal.addModel("KEY", key);
		modal.addModel("TEMPLATE", template);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel deleteView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		ViewModel modal = this.createModal("mDeleteKey");
		modal.addModel("KEY", key);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel editView(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		ViewModel modal = this.addView();
		modal.addModel("KEY", key);
		return modal;
	}
	
	@Override
	@Transactional
	public ViewModel addView() {
		List<ETemplate> templates = this.dTemplate.findList();
		for (ETemplate t : templates) {
			t.getSshkeys().size(); // lazy loading ...
		}
		ViewModel modal = this.createModal("mModKey");
		modal.addModel("TEMPLATES", templates);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect save(String oldOwner, String owner, String key, String[] templates) throws FormErrorException {
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
		return new AjaxRedirect(IWebPath.WEBROOT + ISSHKey.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxRedirect addTemplate(String owner, String[] templates) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		RESTAssert.assertNotNull(key);
		for (String template : templates) {
			ETemplate t = this.dTemplate.findByName(template);
			t.getSshkeys().add(key);
			this.dTemplate.save(t);
		}
		this.audit("Added templates " + this.auditFormat(templates) + " to key " + owner);
		return new AjaxRedirect(IWebPath.WEBROOT + ISSHKey.ROOT, "default");
	}
	
	@Override
	@Transactional
	public AjaxRedirect addKey(String template, String[] keys) {
		RESTAssert.assertNotEmpty(template);
		ETemplate t = this.dTemplate.findByName(template);
		for (String key : keys) {
			ESSHKey ssh = this.dSSH.findByOwner(key);
			t.getSshkeys().add(ssh);
			this.dTemplate.save(t);
		}
		this.audit("Added keys " + this.auditFormat(keys) + " to template " + template);
		return new AjaxRedirect(IWebPath.WEBROOT + ISSHKey.ROOT, "template");
	}
	
	@Override
	@Transactional
	public AjaxRedirect deleteTemplate(String owner, String tname) {
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dTemplate.findByName(tname);
		ESSHKey key = this.dSSH.findByOwner(owner);
		if (template.getSshkeys().contains(key)) {
			template.getSshkeys().remove(key);
			this.dTemplate.save(template);
		}
		return new AjaxRedirect(IWebPath.WEBROOT + ISSHKey.ROOT, "template");
	}
	
	@Override
	@Transactional
	public AjaxRedirect delete(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dSSH.findByOwner(owner);
		this.dSSH.delete(key);
		this.audit("Deleted key " + owner);
		return new AjaxRedirect(IWebPath.WEBROOT + ISSHKey.ROOT);
	}
	
}
