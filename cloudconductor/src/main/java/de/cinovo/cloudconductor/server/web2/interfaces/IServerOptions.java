package de.cinovo.cloudconductor.server.web2.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.taimos.cxf_renderer.model.ViewModel;

@Path(IServerOptions.ROOT)
public interface IServerOptions {
	
	/** the root */
	public static final String ROOT = "/options";
	
	public static final String REMOVE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@POST
	@Path(IWebPath.ACTION_SAVE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect saveOptions(@FormParam("name") String name, @FormParam("bgcolor") String bgcolor, @FormParam("allowautoupdate") String autoUpdate, @FormParam("description") String descr) throws FormErrorException;
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewLinks();
	
	// @GET
	// @Path(IWebPath.ACTION_ADD)
	// @Produces(MediaType.TEXT_HTML)
	// public abstract ViewModel viewAdd();
	//
	// @POST
	// @Path(IWebPath.ACTION_ADD)
	// @Produces(MediaType.TEXT_HTML)
	// public abstract Object add(@FormParam("label") String label, @FormParam("link") String link);
	//
	// @GET
	// @Path(IServerOptions.REMOVE_ACTION)
	// @Produces(MediaType.TEXT_HTML)
	// public abstract ViewModel viewRemove(@PathParam(IWebPath.VAR_NAME) String label);
	//
	// @POST
	// @Path(IServerOptions.REMOVE_ACTION)
	// @Produces(MediaType.TEXT_HTML)
	// public abstract Response delete(@PathParam(IWebPath.VAR_NAME) String label);
	
}
