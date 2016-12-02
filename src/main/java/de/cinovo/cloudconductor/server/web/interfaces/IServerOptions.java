package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.exception.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.RenderedUI;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *		
 */
@Path(IServerOptions.ROOT)
public interface IServerOptions {
	
	/** the root */
	public static final String ROOT = "/options";
	
	/** */
	public static final String LINKS_ROOT = "/links";
	/** */
	public static final String ADD_LINK = IServerOptions.LINKS_ROOT + IWebPath.ACTION_ADD;
	/** */
	public static final String DELETE_LINK = IServerOptions.LINKS_ROOT + "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI view();
	
	/**
	 * @param name the server name
	 * @param bgcolor the server color
	 * @param autoUpdate the auto update flag
	 * @param descr the server description
	 * @param needsapproval the approval flag
	 * @param hostCleanUpTimer hostCleanUpTimer
	 * @param hostCleanUpTimerUnit hostCleanUpTimerUnit
	 * @param indexScanTimer indexScanTimer
	 * @param indexScanTimerUnit indexScanTimerUnit
	 * @param pageRefreshTimer pageRefreshTimer
	 * @param pageRefreshTimerUnit pageRefreshTimerUnit
	 * @param disallowUninstall disallowUninstall
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IWebPath.ACTION_SAVE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer saveOptions(@FormParam("name") String name, @FormParam("bgcolor") String bgcolor, @FormParam("allowautoupdate") String autoUpdate, @FormParam("description") String descr, @FormParam("needsapproval") String needsapproval, @FormParam("hostCleanUpTimer") String hostCleanUpTimer, @FormParam("hostCleanUpTimerUnit") String hostCleanUpTimerUnit, @FormParam("indexScanTimer") String indexScanTimer, @FormParam("indexScanTimerUnit") String indexScanTimerUnit, @FormParam("pageRefreshTimer") String pageRefreshTimer, @FormParam("pageRefreshTimerUnit") String pageRefreshTimerUnit, @FormParam("disallowUninstall") String disallowUninstall) throws FormErrorException;
	
	/**
	 * @return the modal
	 */
	@GET
	@Path(IServerOptions.LINKS_ROOT)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI viewLinks();
	
	/**
	 * @return the modal
	 */
	@GET
	@Path(IServerOptions.ADD_LINK)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addLinkView();
	
	/**
	 * @param label the label
	 * @param link the link (url)
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IServerOptions.ADD_LINK)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer addLink(@FormParam("label") String label, @FormParam("link") String link) throws FormErrorException;
	
	/**
	 * @param label the label
	 * @return the modal content
	 */
	@GET
	@Path(IServerOptions.DELETE_LINK)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteLinkView(@PathParam(IWebPath.VAR_NAME) String label);
	
	/**
	 * @param label the label
	 * @return an ajax answer
	 */
	@POST
	@Path(IServerOptions.DELETE_LINK)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteLink(@PathParam(IWebPath.VAR_NAME) String label);
	
}
