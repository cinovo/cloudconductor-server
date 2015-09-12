package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.RenderedUI;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@Path(IConfig.ROOT)
public interface IConfig {
	
	/** the root */
	public static final String ROOT = "/config";
	/** GLOBAL */
	public static final String RESERVED_GLOBAL = "GLOBAL";
	/***/
	public static final String EDIT_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_EDIT;
	
	/***/
	public static final String DELETE_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	/***/
	public static final String DELETE_SERVICE = "/template/{" + IWebPath.VAR_TEMPLATE + "}/service/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_DELETE;
	/***/
	public static final String DELETE_TEMPLATE = "/template/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_DELETE;
	
	/***/
	public static final String ADD_KV_TEMPLATE = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	/***/
	public static final String ADD_KV_SERVICE = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_ADD;
	
	/***/
	public static final String SAVE_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_SAVE;
	/** batch mode */
	public static final String BATCH_ACTION = "/batchmod";
	
	
	/**
	 * @param viewtype the viewtype
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI view(@QueryParam("viewtype") String viewtype);
	
	/**
	 * @param id the config id
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.DELETE_KV_PAIR)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteConfigView(@PathParam(IWebPath.VAR_ID) String id);
	
	/**
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.DELETE_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteTemplateView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.DELETE_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteServiceView(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	/**
	 * @param id the config id
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.EDIT_KV_PAIR)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI editConfigView(@PathParam(IWebPath.VAR_ID) String id);
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addConfigView();
	
	/**
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.ADD_KV_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addConfigView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param template the template name
	 * @param service the service
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.ADD_KV_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addConfigView(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI batchModView();
	
	/**
	 * @param oldId the id of the old config, may be 0 if a new config is created
	 * @param template the template name
	 * @param service the servic name
	 * @param key the key
	 * @param value the value
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IConfig.SAVE_KV_PAIR)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer save(@PathParam(IWebPath.VAR_ID) String oldId, @FormParam(IWebPath.VAR_TEMPLATE) String template, @FormParam(IWebPath.VAR_SERVICE) String service, @FormParam("key") String key, @FormParam("value") String value) throws FormErrorException;
	
	/**
	 * @param id the config id
	 * @return an ajax answer
	 */
	@POST
	@Path(IConfig.DELETE_KV_PAIR)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteConfig(@PathParam(IWebPath.VAR_ID) String id);
	
	/**
	 * @param template the template name
	 * @return an ajax answer
	 */
	@POST
	@Path(IConfig.DELETE_TEMPLATE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @return an ajax answer
	 */
	@POST
	@Path(IConfig.DELETE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteService(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	/**
	 * @param batch the batch string
	 * @return an ajax answer
	 */
	@POST
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer batchMod(@FormParam("batch") String batch);
}
