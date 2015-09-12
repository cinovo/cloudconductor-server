package de.cinovo.cloudconductor.server.web.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer.AjaxAnswerType;
import de.cinovo.cloudconductor.server.web.interfaces.IPackageServer;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.RenderedUI;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
public class PackageServerImpl extends AWebPage implements IPackageServer {
	
	@Autowired
	private IPackageServerDAO dPckSrv;
	@Autowired
	private ITemplateDAO dTemplate;
	
	
	@Override
	protected String getTemplateFolder() {
		return "servers";
	}
	
	@Override
	protected void init() {
		// nothing to do
	}
	
	@Override
	protected String getNavElementName() {
		return "Package Servers";
	}
	
	@Override
	public RenderedUI view() {
		final CSViewModel modal = this.createModal("mServers");
		modal.addModel("servers", this.dPckSrv.findList());
		return modal.render();
	}
	
	@Override
	public RenderedUI addServerView() {
		final CSViewModel modal = this.createModal("mModServer");
		return modal.render();
	}
	
	@Override
	public RenderedUI editServerView(Long serverid) {
		EPackageServer server = this.dPckSrv.findById(serverid);
		RESTAssert.assertNotNull(server);
		final CSViewModel modal = this.createModal("mModServer");
		modal.addModel("server", server);
		return modal.render();
	}
	
	@Override
	public RenderedUI deleteServerView(Long serverid) {
		EPackageServer server = this.dPckSrv.findById(serverid);
		RESTAssert.assertNotNull(server);
		final CSViewModel modal = this.createModal("mDeleteServer");
		modal.addModel("server", server);
		return modal.render();
	}
	
	@Override
	public AjaxAnswer saveServer(Long serverid, String path, String description) throws FormErrorException {
		RESTAssert.assertNotNull(serverid);
		FormErrorException error = null;
		error = this.assertNotEmpty(path, error, "path");
		error = this.assertNotEmpty(description, error, "description");
		if (error != null) {
			// add the currently entered values to the answer
			error.addFormParam("path", path);
			error.addFormParam("description", description);
			if (serverid > 0) {
				error.setParentUrl(IPackageServer.ROOT, serverid.toString(), IWebPath.ACTION_EDIT);
			} else {
				error.setParentUrl(IPackageServer.ROOT, IWebPath.ACTION_ADD);
			}
			throw error;
		}
		
		EPackageServer server = this.dPckSrv.findById(serverid);
		if (server == null) {
			server = new EPackageServer();
		}
		server.setDescription(description);
		server.setPath(path);
		server = this.dPckSrv.save(server);
		
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IPackageServer.ROOT, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	@Override
	public AjaxAnswer deleteServer(Long serverid) throws FormErrorException {
		RESTAssert.assertNotNull(serverid);
		EPackageServer server = this.dPckSrv.findById(serverid);
		List<ETemplate> tmplt = this.dTemplate.findByPackageServer(serverid);
		if ((tmplt != null) && (tmplt.size() > 0)) {
			FormErrorException error = this.createError("The package server is still in use and can't be deleted");
			error.setParentUrl(IPackageServer.ROOT);
			throw error;
		}
		this.dPckSrv.delete(server);
		AjaxAnswer ajaxRedirect = new AjaxAnswer(IWebPath.WEBROOT + IPackageServer.ROOT, AjaxAnswerType.GET);
		ajaxRedirect.setInfo("Successfully deleted");
		return ajaxRedirect;
	}
	
}
