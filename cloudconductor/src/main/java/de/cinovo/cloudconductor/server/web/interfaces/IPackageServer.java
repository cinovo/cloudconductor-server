package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
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
	
	/** */
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	/** */
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_EDIT;
	/** */
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_SAVE;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel view();
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel addServerView();
	
	/**
	 * @param serverid the package server id
	 * @return the modal content
	 */
	@GET
	@Path(IPackageServer.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel editServerView(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	/**
	 * @param serverid the package server id
	 * @return the modal content
	 */
	@GET
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel deleteServerView(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	/**
	 * @param serverid the package server id
	 * @param path the path (url)
	 * @param description the description of the package server
	 * @return an ajax answer
	 * @throws FormErrorException one form errors
	 */
	@POST
	@Path(IPackageServer.SAVE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer saveServer(@PathParam(IWebPath.VAR_ID) Long serverid, @FormParam("path") String path, @FormParam("description") String description) throws FormErrorException;
	
	/**
	 * @param serverid the package server id
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer deleteServer(@PathParam(IWebPath.VAR_ID) Long serverid) throws FormErrorException;
}
