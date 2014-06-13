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
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IServices.ROOT)
public interface IServices {
	
	/** the root */
	public static final String ROOT = "/services";
	
	/** */
	public static final String SAVE_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	/** */
	public static final String EDIT_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	/** */
	public static final String DELETE_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	/** */
	public static final String ADD_PACKAGE = "/{" + IWebPath.VAR_NAME + "}/package" + IWebPath.ACTION_ADD;
	/** */
	public static final String DELETE_PACKAGE = "/{" + IWebPath.VAR_NAME + "}/package/{" + IWebPath.VAR_PKG + "}" + IWebPath.ACTION_DELETE;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView view();
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView newServiceView();
	
	/**
	 * @param service the service name
	 * @return the modal content
	 */
	@GET
	@Path(IServices.EDIT_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView editServiceView(@PathParam(IWebPath.VAR_NAME) String service);
	
	/**
	 * @param service the service name
	 * @return the modal content
	 */
	@GET
	@Path(IServices.DELETE_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView deleteServiceView(@PathParam(IWebPath.VAR_NAME) String service);
	
	/**
	 * @param service the service name
	 * @param pkg the package name
	 * @return the modal content
	 */
	@GET
	@Path(IServices.DELETE_PACKAGE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView deletePackageView(@PathParam(IWebPath.VAR_NAME) String service, @PathParam(IWebPath.VAR_PKG) String pkg);
	
	/**
	 * @param service the service name
	 * @return the modal content
	 */
	@GET
	@Path(IServices.ADD_PACKAGE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedView addPackageView(@PathParam(IWebPath.VAR_NAME) String service);
	
	/**
	 * @param service the service name
	 * @param newservice the new service name
	 * @param initscript the initscript name
	 * @param description the description of the service
	 * @param pkgs associated package names
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IServices.SAVE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer saveService(@PathParam(IWebPath.VAR_NAME) String service, @FormParam("name") String newservice, @FormParam("script") String initscript, @FormParam("description") String description, @FormParam("pkgs") String[] pkgs) throws FormErrorException;
	
	/**
	 * @param service the service name
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IServices.DELETE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer deleteService(@PathParam(IWebPath.VAR_NAME) String service) throws FormErrorException;
	
	/**
	 * @param service the service names
	 * @param pkg the package name
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IServices.DELETE_PACKAGE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer deletePackage(@PathParam(IWebPath.VAR_NAME) String service, @PathParam(IWebPath.VAR_PKG) String pkg) throws FormErrorException;
	
	/**
	 * @param service the service name
	 * @param pkgs the package names
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IServices.ADD_PACKAGE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer addPackage(@PathParam(IWebPath.VAR_NAME) String service, @FormParam("pkgs") String[] pkgs) throws FormErrorException;
}
