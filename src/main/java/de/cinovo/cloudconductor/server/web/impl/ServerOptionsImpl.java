package de.cinovo.cloudconductor.server.web.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.ServerTaskHelper;
import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.util.ICCProperties;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer.AjaxAnswerType;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.helper.NavbarRegistry;
import de.cinovo.cloudconductor.server.web.interfaces.IServerOptions;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class ServerOptionsImpl extends AWebPage implements IServerOptions {
	
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected NavbarRegistry navReg;
	@Autowired
	protected ServerTaskHelper taskHelper;
	
	
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
	public RenderedView view() {
		final CSViewModel modal = this.createModal("mOptions");
		EServerOptions options = this.dServerOptions.get();
		modal.addModel("options", options);
		modal.addModel("timeUnits", TimeUnit.values());
		modal.addModel("indexScanAvailable", System.getProperty(ICCProperties.REPO_SCAN, "false"));
		StringBuilder disallowUninstall = new StringBuilder();
		for (String string : options.getDisallowUninstall()) {
			disallowUninstall.append(string);
			disallowUninstall.append(System.lineSeparator());
		}
		modal.addModel("disallowUninstall", disallowUninstall.toString());
		return modal.render();
	}
	
	@Override
	public RenderedView viewLinks() {
		final CSViewModel modal = this.createModal("mLinks");
		modal.addModel("links", this.dLinks.findList());
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer saveOptions(String name, String bgcolor, String autoUpdate, String descr, String needsapproval, String hostCleanUpTimer, String hostCleanUpTimerUnit, String indexScanTimer, String indexScanTimerUnit, String pageRefreshTimer, String pageRefreshTimerUnit, String disallowUninstall) throws FormErrorException {
		FormErrorException error = null;
		error = this.assertNotEmpty(name, error, "name");
		error = this.assertNotEmpty(bgcolor, error, "bgcolor");
		error = this.assertNotEmpty(hostCleanUpTimer, error, "hostCleanUpTimer");
		error = this.assertNotEmpty(hostCleanUpTimerUnit, error, "hostCleanUpTimerUnit");
		error = this.assertNotEmpty(pageRefreshTimer, error, "pageRefreshTimer");
		error = this.assertNotEmpty(pageRefreshTimerUnit, error, "pageRefreshTimerUnit");
		if (System.getProperty(ICCProperties.REPO_SCAN, "false").equals("true")) {
			error = this.assertNotEmpty(indexScanTimer, error, "indexScanTimer");
			error = this.assertNotEmpty(indexScanTimerUnit, error, "indexScanTimerUnit");
		}
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("name", name);
			error.addFormParam("bgcolor", bgcolor);
			error.addFormParam("allowautoupdate", autoUpdate);
			error.addFormParam("needsapproval", needsapproval);
			error.addFormParam("hostCleanUpTimer", hostCleanUpTimer);
			error.addFormParam("hostCleanUpTimerUnit", hostCleanUpTimerUnit);
			error.addFormParam("indexScanTimer", indexScanTimer);
			error.addFormParam("indexScanTimerUnit", indexScanTimerUnit);
			error.addFormParam("pageRefreshTimer", pageRefreshTimer);
			error.addFormParam("pageRefreshTimerUnit", pageRefreshTimerUnit);
			error.addFormParam("disallowUninstall", disallowUninstall);
			error.addFormParam("description", descr);
			error.setParentUrl(IServerOptions.ROOT);
			throw error;
		}
		
		EServerOptions options = this.dServerOptions.get();
		EServerOptions oldOptions = options;
		options.setName(name);
		options.setBgcolor(bgcolor);
		options.setAllowautoupdate(autoUpdate == null ? false : true);
		options.setNeedsApproval(needsapproval == null ? false : true);
		options.setDescription(descr);
		options.setHostCleanUpTimer(Integer.valueOf(hostCleanUpTimer));
		options.setHostCleanUpTimerUnit(TimeUnit.valueOf(hostCleanUpTimerUnit));
		if ((indexScanTimer != null) && !indexScanTimer.isEmpty()) {
			options.setIndexScanTimer(Integer.valueOf(indexScanTimer));
			options.setIndexScanTimerUnit(TimeUnit.valueOf(indexScanTimerUnit));
		}
		options.setPageRefreshTimer(Integer.valueOf(pageRefreshTimer));
		options.setPageRefreshTimerUnit(TimeUnit.valueOf(pageRefreshTimerUnit));

		Set<String> disallowed = new HashSet<>();
		for (String string : disallowUninstall.split(System.lineSeparator())) {
			if (!string.trim().isEmpty()) {
				disallowed.add(string);
			}
		}
		options.setDisallowUninstall(disallowed);

		options = this.dServerOptions.save(options);

		this.handleGlobalDisableAutoUpdate(options);
		this.taskHelper.updateTasks(oldOptions);

		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IServerOptions.ROOT, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	private void handleGlobalDisableAutoUpdate(EServerOptions options) {
		if (!options.isAllowautoupdate()) {
			for (ETemplate t : this.dTemplate.findList()) {
				if (t.getAutoUpdate()) {
					t.setAutoUpdate(false);
					this.dTemplate.save(t);
				}
			}
		}
	}
	
	@Override
	public RenderedView addLinkView() {
		final CSViewModel modal = this.createModal("mAddLink");
		return modal.render();
	}
	
	@Override
	public AjaxAnswer addLink(String label, String link) throws FormErrorException {
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
		
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IServerOptions.ROOT + IServerOptions.LINKS_ROOT, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	@Override
	public RenderedView deleteLinkView(String label) {
		EAdditionalLinks link = this.dLinks.findByLabel(label);
		RESTAssert.assertNotNull(link);
		final CSViewModel modal = this.createModal("mDeleteLink");
		modal.addModel("link", link);
		return modal.render();
	}
	
	@Override
	public AjaxAnswer deleteLink(String label) {
		EAdditionalLinks link = this.dLinks.findByLabel(label);
		RESTAssert.assertNotNull(link);
		this.dLinks.delete(link);
		this.navReg.unregisterSubMenu(NavbarHardLinks.links, link.getLabel());
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IServerOptions.ROOT + IServerOptions.LINKS_ROOT, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("The link " + label + " has been deleted.");
		return ajaxRedirect;
	}
	
}
