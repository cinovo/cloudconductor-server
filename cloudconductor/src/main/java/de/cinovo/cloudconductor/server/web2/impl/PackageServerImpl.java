package de.cinovo.cloudconductor.server.web2.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect.AjaxRedirectType;
import de.cinovo.cloudconductor.server.web2.interfaces.IPackageServer;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

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
	public ViewModel view() {
		final ViewModel modal = this.createModal("mServers");
		modal.addModel("servers", this.dPckSrv.findList());
		return modal;
	}
	
	@Override
	public ViewModel addServerView() {
		final ViewModel modal = this.createModal("mModServer");
		return modal;
	}
	
	@Override
	public ViewModel editServerView(Long serverid) {
		EPackageServer server = this.dPckSrv.findById(serverid);
		RESTAssert.assertNotNull(server);
		final ViewModel modal = this.createModal("mModServer");
		modal.addModel("server", server);
		return modal;
	}
	
	@Override
	public ViewModel deleteServerView(Long serverid) {
		EPackageServer server = this.dPckSrv.findById(serverid);
		RESTAssert.assertNotNull(server);
		final ViewModel modal = this.createModal("mDeleteServer");
		modal.addModel("server", server);
		return modal;
	}
	
	@Override
	public AjaxRedirect saveServer(Long serverid, String path, String description) throws FormErrorException {
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
		
		AjaxRedirect ajaxRedirect = new AjaxRedirect(IWebPath.WEBROOT + IPackageServer.ROOT, AjaxRedirectType.GET);
		ajaxRedirect.setInfo("Successfully saved");
		return ajaxRedirect;
	}
	
	@Override
	public AjaxRedirect deleteServer(Long serverid) throws FormErrorException {
		RESTAssert.assertNotNull(serverid);
		EPackageServer server = this.dPckSrv.findById(serverid);
		List<ETemplate> tmplt = this.dTemplate.findByPackageServer(serverid);
		if ((tmplt != null) && (tmplt.size() > 0)) {
			FormErrorException error = this.createError("The package server is still in use and can't be deleted");
			error.setParentUrl(IPackageServer.ROOT);
			throw error;
		}
		this.dPckSrv.delete(server);
		AjaxRedirect ajaxRedirect = new AjaxRedirect(IWebPath.WEBROOT + IPackageServer.ROOT, AjaxRedirectType.GET);
		ajaxRedirect.setInfo("Successfully deleted");
		return ajaxRedirect;
	}
	
}
