package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
@Path(IFiles.ROOT)
public interface IFiles {
	
	/** a viewtype for template view */
	public static final String TEMPLATE_FILTER = "template";
	
	/** the root */
	public static final String ROOT = "/files";
	
	/***/
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	/***/
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	/***/
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	/***/
	public static final String ADD_TO_FILE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + "/templates" + IWebPath.ACTION_ADD;
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
	public RenderedUI newFileView();
	
	/**
	 * @param name the file name
	 * @return the modal content
	 */
	@GET
	@Path(IFiles.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI editFileView(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the file name
	 * @return the modal content
	 */
	@GET
	@Path(IFiles.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI deleteFileView(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the file name
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IFiles.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI deleteFileFromTemplateView(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(IFiles.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI addFileToTemplateView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param file the file name
	 * @return the modal content
	 */
	@GET
	@Path(IFiles.ADD_TO_FILE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI addTemplateToFileView(@PathParam(IWebPath.VAR_NAME) String file);
	
	/**
	 * @param oldname the old file name
	 * @param newname the new file name
	 * @param owner the owner
	 * @param group the group
	 * @param mode the file mode
	 * @param targetPath the target path
	 * @param content the file content
	 * @param isTemplate file is template
	 * @param depPackage name of dependent package
	 * @param depServices names of associated services
	 * @param templates names of associated templates
	 * @return an ajax answer
	 * @throws FormErrorException on error within the form
	 */
	@POST
	@Path(IFiles.SAVE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer saveFile(@PathParam(IWebPath.VAR_NAME) String oldname, @FormParam("name") String newname, @FormParam("owner") String owner, @FormParam("group") String group, @FormParam("mode") String mode, @FormParam("targetPath") String targetPath, @FormParam("file_content") String content, @FormParam("isTemplate") Boolean isTemplate, @FormParam("depPackage") String depPackage, @FormParam("depServices") String[] depServices, @FormParam("templates") String[] templates) throws FormErrorException;
	
	/**
	 * @param name the file name
	 * @return an ajax answer
	 */
	@POST
	@Path(IFiles.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer deleteFile(@PathParam(IWebPath.VAR_NAME) String name);
	
	/**
	 * @param name the file name
	 * @param template the template name
	 * @return an ajax answer
	 */
	@POST
	@Path(IFiles.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer deleteFileFromTemplate(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param name array of file names
	 * @param template the template anem
	 * @return an ajax answer
	 */
	@POST
	@Path(IFiles.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer addFileToTemplate(@FormParam("files") String[] name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param name array of file names
	 * @param template the template anem
	 * @return an ajax answer
	 */
	@POST
	@Path(IFiles.ADD_TO_FILE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer addTemplateToFile(@FormParam("templates") String[] template, @PathParam(IWebPath.VAR_NAME) String name);
}
