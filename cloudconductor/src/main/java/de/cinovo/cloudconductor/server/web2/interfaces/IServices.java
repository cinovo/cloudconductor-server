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

@Path(IServices.ROOT)
public interface IServices {
	
	/** the root */
	public static final String ROOT = "/services";
	public static final String SAVE_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	public static final String EDIT_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	public static final String DELETE_SERVICE = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	public static final String ADD_PACKAGE = "/{" + IWebPath.VAR_NAME + "}/package" + IWebPath.ACTION_ADD;
	public static final String DELETE_PACKAGE = "/{" + IWebPath.VAR_NAME + "}/package/{" + IWebPath.VAR_PKG + "}" + IWebPath.ACTION_DELETE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel view();
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel newServiceView();
	
	@GET
	@Path(IServices.EDIT_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel editServiceView(@PathParam(IWebPath.VAR_NAME) String service);
	
	@GET
	@Path(IServices.DELETE_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel deleteServiceView(@PathParam(IWebPath.VAR_NAME) String service);
	
	@GET
	@Path(IServices.DELETE_PACKAGE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel deletePackageView(@PathParam(IWebPath.VAR_NAME) String service, @PathParam(IWebPath.VAR_PKG) String pkg);
	
	@GET
	@Path(IServices.ADD_PACKAGE)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel addPackageView(@PathParam(IWebPath.VAR_NAME) String service);
	
	@POST
	@Path(IServices.SAVE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect saveService(@PathParam(IWebPath.VAR_NAME) String service, @FormParam("name") String newservice, @FormParam("script") String initscript, @FormParam("description") String description, @FormParam("pkgs") String[] pkgs) throws FormErrorException;
	
	@POST
	@Path(IServices.DELETE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect deleteService(@PathParam(IWebPath.VAR_NAME) String service) throws FormErrorException;
	
	@POST
	@Path(IServices.DELETE_PACKAGE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect deletePackage(@PathParam(IWebPath.VAR_NAME) String service, @PathParam(IWebPath.VAR_PKG) String pkg) throws FormErrorException;
	
	@POST
	@Path(IServices.ADD_PACKAGE)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxRedirect addPackage(@PathParam(IWebPath.VAR_NAME) String service, @FormParam("pkgs") String[] pkgs) throws FormErrorException;
}
