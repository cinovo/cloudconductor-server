package de.cinovo.cloudconductor.server.web.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.RenderedUI;

import javax.ws.rs.*;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@Path(IDirectories.ROOT)
public interface IDirectories {
	
	/** a viewtype for template view */
	public static final String TEMPLATE_FILTER = "template";
	
	/** the root */
	public static final String ROOT = "/directories";
	
	/***/
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	/***/
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	/***/
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	/***/
	public static final String ADD_TO_DIR_ACTION = "/{" + IWebPath.VAR_NAME + "}" + "/templates" + IWebPath.ACTION_ADD;
	/***/
	public static final String ADD_TO_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	/***/
	public static final String REMOVE_FROM_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	/**
	 * @param viewtype the viewtype
	 * @param filter the filter
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI view(@QueryParam("viewtype") String viewtype, @QueryParam("filter") String[] filter);
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI newDirectoryView();
	
	/**
	 * @param name the dir name
	 * @return the modal content
	 */
	@GET
	@Path(IDirectories.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI editDirectoryView(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the dir name
	 * @return the modal content
	 */
	@GET
	@Path(IDirectories.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI deleteDirectoryView(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the dir name
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IDirectories.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI deleteDirectoryFromTemplateView(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IDirectories.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI addDirectoryToTemplateView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param dir the dir name
	 * @return the modal content
	 */
	@GET
	@Path(IDirectories.ADD_TO_DIR_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI addTemplateToDirectoryView(@PathParam(IWebPath.VAR_NAME) String dir);
	
	/**
	 * @param oldname the old file name
	 * @param newname the new file name
	 * @param owner the owner
	 * @param group the group
	 * @param mode the file mode
	 * @param targetPath the target path
	 * @param depPackage name of dependent package
	 * @param depServices names of associated services
	 * @param templates names of associated templates
	 * @return an ajax answer
	 * @throws FormErrorException on error within the form
	 */
	@POST
	@Path(IDirectories.SAVE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer saveDirectory(@PathParam(IWebPath.VAR_NAME) String oldname, @FormParam("name") String newname, @FormParam("owner") String owner, @FormParam("group") String group, @FormParam("mode") String mode, @FormParam("targetPath") String targetPath, @FormParam("depPackage") String depPackage, @FormParam("depServices") String[] depServices, @FormParam("templates") String[] templates) throws FormErrorException;
	
	/**
	 * @param name the file name
	 * @return an ajax answer
	 */
	@POST
	@Path(IDirectories.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer deleteDirectory(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the file name
	 * @param template the template name
	 * @return an ajax answer
	 */
	@POST
	@Path(IDirectories.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer deleteDirectoryFromTemplate(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param name array of dir names
	 * @param template the template anem
	 * @return an ajax answer
	 */
	@POST
	@Path(IDirectories.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer addDirectoryToTemplate(@FormParam("files") String[] name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param name array of dir names
	 * @param template the template anem
	 * @return an ajax answer
	 */
	@POST
	@Path(IDirectories.ADD_TO_DIR_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer addTemplateToDirectory(@FormParam("templates") String[] template, @PathParam(IWebPath.VAR_NAME) String name);
}
