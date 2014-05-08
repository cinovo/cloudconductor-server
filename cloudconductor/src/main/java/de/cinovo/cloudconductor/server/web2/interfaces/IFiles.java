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
import de.cinovo.cloudconductor.server.web.interfaces.IFile;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.taimos.cxf_renderer.model.ViewModel;

@Path(IFile.ROOT)
public interface IFiles {
	
	public static final String TEMPLATE_FILTER = "template";
	
	/** the root */
	public static final String ROOT = "/files";
	
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	
	public static final String ADD_TO_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	public static final String REMOVE_FROM_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel view(@QueryParam("filter") String filter);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel newFileView();
	
	@GET
	@Path(IFiles.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel editFileView(@PathParam(IWebPath.VAR_NAME) String name);
	
	@GET
	@Path(IFiles.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel deleteFileView(@PathParam(IWebPath.VAR_NAME) String name);
	
	@GET
	@Path(IFiles.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel deleteFileFromTemplateView(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IFiles.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public ViewModel addFileToTemplateView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@POST
	@Path(IFiles.SAVE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxRedirect saveFile(@PathParam(IWebPath.VAR_NAME) String oldname, @FormParam("name") String newname, @FormParam("owner") String owner, @FormParam("group") String group, @FormParam("mode") String mode, @FormParam("targetPath") String targetPath, @FormParam("file_content") String content, @FormParam("isTemplate") Boolean isTemplate, @FormParam("depPackage") String depPackage, @FormParam("depServices") String[] depServices, @FormParam("templates") String[] templates) throws FormErrorException;
	
	@POST
	@Path(IFiles.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxRedirect deleteFile(@PathParam(IWebPath.VAR_NAME) String name);
	
	@POST
	@Path(IFiles.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxRedirect deleteFileFromTemplate(@PathParam(IWebPath.VAR_NAME) String name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@POST
	@Path(IFiles.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxRedirect addFileToTemplate(@FormParam("files") String[] name, @PathParam(IWebPath.VAR_TEMPLATE) String template);
}
