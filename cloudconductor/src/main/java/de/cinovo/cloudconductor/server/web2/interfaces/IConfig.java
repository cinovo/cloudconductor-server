package de.cinovo.cloudconductor.server.web2.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.taimos.cxf_renderer.model.ViewModel;

@Path(IConfig.ROOT)
public interface IConfig {
	
	/** the root */
	public static final String ROOT = "/config";
	/** GLOBAL */
	public static final String RESERVED_GLOBAL = "GLOBAL";
	
	public static final String EDIT_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_EDIT;
	
	public static final String DELETE_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	public static final String DELETE_SERVICE = "/template/{" + IWebPath.VAR_TEMPLATE + "}/service/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_DELETE;
	public static final String DELETE_TEMPLATE = "/template/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_DELETE;
	
	public static final String ADD_KV_TEMPLATE = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	public static final String ADD_KV_SERVICE = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_ADD;
	
	public static final String SAVE_KV_PAIR = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_SAVE;
	/** batch mode */
	public static final String BATCH_ACTION = "/batchmod";
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view(@QueryParam("filter") String filter);
	
	@GET
	@Path(IConfig.DELETE_KV_PAIR)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel deleteConfigView(@PathParam(IWebPath.VAR_ID) String id);
	
	@GET
	@Path(IConfig.DELETE_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel deleteTemplateView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IConfig.DELETE_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel deleteServiceView(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	@GET
	@Path(IConfig.EDIT_KV_PAIR)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel editConfigView(@PathParam(IWebPath.VAR_ID) String id);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel addConfigView();
	
	@GET
	@Path(IConfig.ADD_KV_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel addConfigView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IConfig.ADD_KV_SERVICE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel addConfigView(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	@GET
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel batchModView();
	
	@POST
	@Path(IConfig.SAVE_KV_PAIR)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect save(@PathParam(IWebPath.VAR_ID) String oldId, @FormParam(IWebPath.VAR_TEMPLATE) String template, @FormParam(IWebPath.VAR_SERVICE) String service, @FormParam("key") String key, @FormParam("value") String value) throws FormErrorException;
	
	@POST
	@Path(IConfig.DELETE_KV_PAIR)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect deleteConfig(@PathParam(IWebPath.VAR_ID) String id);
	
	@POST
	@Path(IConfig.DELETE_TEMPLATE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect deleteTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@POST
	@Path(IConfig.DELETE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect deleteService(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	@POST
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect batchMod(@FormParam("batch") String batch);
}
