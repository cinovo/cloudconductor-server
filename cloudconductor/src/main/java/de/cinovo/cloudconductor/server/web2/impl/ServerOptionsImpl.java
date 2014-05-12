package de.cinovo.cloudconductor.server.web2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect.AjaxRedirectType;
import de.cinovo.cloudconductor.server.web2.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web2.helper.NavbarRegistry;
import de.cinovo.cloudconductor.server.web2.interfaces.IServerOptions;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

public class ServerOptionsImpl extends AWebPage implements IServerOptions {
	
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected NavbarRegistry navReg;
	
	
	@Override
	protected String getTemplateFolder() {
		return "options";
	}
	
	@Override
	protected void init() {
		// nothing to do
	}
	
	@Override
	protected String getNavElementName() {
		return "Options";
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		final ViewModel modal = this.createModal("mOptions");
		modal.addModel("options", this.dServerOptions.get());
		return modal;
	}
	
	@Override
	public ViewModel viewLinks() {
		final ViewModel modal = this.createModal("mLinks");
		modal.addModel("links", this.dLinks.findList());
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect saveOptions(String name, String bgcolor, String autoUpdate, String descr, String needsapproval) throws FormErrorException {
		FormErrorException error = null;
		error = this.assertNotEmpty(name, error, "name");
		error = this.assertNotEmpty(bgcolor, error, "bgcolor");
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", name);
			error.addFormParam("bgcolor", bgcolor);
			error.addFormParam("allowautoupdate", autoUpdate);
			error.addFormParam("needsapproval", needsapproval);
			error.addFormParam("description", descr);
			error.setParentUrl(IServerOptions.ROOT);
			throw error;
		}
		
		EServerOptions options = this.dServerOptions.get();
		options.setName(name);
		options.setBgcolor(bgcolor);
		options.setAllowautoupdate(autoUpdate == null ? false : true);
		options.setNeedsApproval(needsapproval == null ? false : true);
		options.setDescription(descr);
		this.dServerOptions.save(options);
		
		if (!options.isAllowautoupdate()) {
			for (ETemplate t : this.dTemplate.findList()) {
				if (t.getAutoUpdate()) {
					t.setAutoUpdate(false);
					this.dTemplate.save(t);
				}
			}
		}
		AjaxRedirect ajaxRedirect = new AjaxRedirect(IWebPath.WEBROOT + IServerOptions.ROOT, AjaxRedirectType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	@Override
	public ViewModel addLinkView() {
		final ViewModel modal = this.createModal("mAddLink");
		return modal;
	}
	
	@Override
	public AjaxRedirect addLink(String label, String link) throws FormErrorException {
		FormErrorException error = null;
		error = this.assertNotEmpty(label, error, "label");
		error = this.assertNotEmpty(link, error, "link");
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("label", label);
			error.addFormParam("link", link);
			error.setParentUrl(IServerOptions.ROOT, IServerOptions.ADD_LINK);
			throw error;
		}
		EAdditionalLinks add = new EAdditionalLinks();
		add.setLabel(label);
		add.setUrl(link);
		add = this.dLinks.save(add);
		
		this.navReg.registerSubMenu(NavbarHardLinks.links, add.getLabel(), add.getUrl());
		
		AjaxRedirect ajaxRedirect = new AjaxRedirect(IWebPath.WEBROOT + IServerOptions.ROOT + IServerOptions.LINKS_ROOT, AjaxRedirectType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	@Override
	public ViewModel deleteLinkView(String label) {
		EAdditionalLinks link = this.dLinks.findByLabel(label);
		RESTAssert.assertNotNull(link);
		final ViewModel modal = this.createModal("mDeleteLink");
		modal.addModel("link", link);
		return modal;
	}
	
	@Override
	public AjaxRedirect deleteLink(String label) {
		EAdditionalLinks link = this.dLinks.findByLabel(label);
		RESTAssert.assertNotNull(link);
		this.dLinks.delete(link);
		this.navReg.unregisterSubMenu(NavbarHardLinks.links, link.getLabel());
		AjaxRedirect ajaxRedirect = new AjaxRedirect(IWebPath.WEBROOT + IServerOptions.ROOT + IServerOptions.LINKS_ROOT, AjaxRedirectType.GET);
		ajaxRedirect.setInfo("The link " + label + " has been deleted.");
		return ajaxRedirect;
	}
	
}
