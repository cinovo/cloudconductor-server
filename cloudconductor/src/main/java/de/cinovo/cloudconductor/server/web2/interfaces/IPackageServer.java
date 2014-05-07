package de.cinovo.cloudconductor.server.web2.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IPackageServer.ROOT)
public interface IPackageServer {
	
	/** the root */
	public static final String ROOT = "/pkgsrv";
	
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_EDIT;
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_SAVE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel view();
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel addServerView();
	
	@GET
	@Path(IPackageServer.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel editServerView(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	@GET
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel deleteServerView(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	@POST
	@Path(IPackageServer.SAVE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect saveServer(@PathParam(IWebPath.VAR_ID) Long serverid, @FormParam("path") String path, @FormParam("description") String description) throws FormErrorException;
	
	@POST
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect deleteServer(@PathParam(IWebPath.VAR_ID) Long serverid) throws FormErrorException;
}
