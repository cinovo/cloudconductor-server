package de.cinovo.cloudconductor.server.web2.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
	
	public static final String LINKS_ROOT = "/links";
	public static final String ADD_LINK = IServerOptions.LINKS_ROOT + IWebPath.ACTION_ADD;
	public static final String DELETE_LINK = IServerOptions.LINKS_ROOT + "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@POST
	@Path(IWebPath.ACTION_SAVE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect saveOptions(@FormParam("name") String name, @FormParam("bgcolor") String bgcolor, @FormParam("allowautoupdate") String autoUpdate, @FormParam("description") String descr, @FormParam("needsapproval") String needsapproval) throws FormErrorException;
	
	@GET
	@Path(IServerOptions.LINKS_ROOT)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewLinks();
	
	@GET
	@Path(IServerOptions.ADD_LINK)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel addLinkView();
	
	@POST
	@Path(IServerOptions.ADD_LINK)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect addLink(@FormParam("label") String label, @FormParam("link") String link) throws FormErrorException;
	
	@GET
	@Path(IServerOptions.DELETE_LINK)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel deleteLinkView(@PathParam(IWebPath.VAR_NAME) String label);
	
	@POST
	@Path(IServerOptions.DELETE_LINK)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect deleteLink(@PathParam(IWebPath.VAR_NAME) String label);
	
}
