package de.cinovo.cloudconductor.server.web2.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect.AjaxRedirectType;
import de.cinovo.cloudconductor.server.web2.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web2.interfaces.IServerOptions;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;

public class ServerOptionsImpl extends AWebPage implements IServerOptions {
	
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	@Autowired
	protected ITemplateDAO dTemplate;
	
	
	@Override
	protected String getTemplateFolder() {
		return "options";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IServerOptions.ROOT, 100);
		this.addBreadCrumb(IWebPath.WEBROOT + IServerOptions.ROOT, this.getNavElementName());
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
	public AjaxRedirect saveOptions(String name, String bgcolor, String autoUpdate, String descr) throws FormErrorException {
		String errorMessage = "Please fill in all the information.";
		FormErrorException error = null;
		error = this.checkForEmpty(name, errorMessage, error, "name");
		error = this.checkForEmpty(bgcolor, errorMessage, error, "bgcolor");
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", name);
			error.addFormParam("bgcolor", bgcolor);
			error.addFormParam("allowautoupdate", autoUpdate);
			error.addFormParam("description", descr);
			error.setParentUrl(IServerOptions.ROOT);
			throw error;
		}
		
		EServerOptions options = this.dServerOptions.get();
		options.setName(name);
		options.setBgcolor(bgcolor);
		options.setAllowautoupdate(autoUpdate == null ? false : true);
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
	
}
