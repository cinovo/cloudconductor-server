package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
@Path(IFileTags.ROOT)
public interface IFileTags {
	
	/** the root */
	public static final String ROOT = "/tags";
	
	/** */
	public static final String DELETE_TAG = "/{" + IWebPath.VAR_ID + "}" + "/{tag" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	
	/** */
	public static final String ADD_TAG = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_NEW;
	
	/** */
	public static final String TAGED_FILE = "/{" + IWebPath.VAR_ID + "}";
	
	
	/**
	 * @param fileid the file id
	 * @return the view
	 */
	@GET
	@Path(IFileTags.TAGED_FILE)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView viewFilesTags(@PathParam(IWebPath.VAR_ID) Long fileid);
	
	/**
	 * @param fileid the file id
	 * @return the view
	 */
	@GET
	@Path(IFileTags.ADD_TAG)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView newTagView(@PathParam(IWebPath.VAR_ID) Long fileid);
	
	/**
	 * @param fileid the file id
	 * @param tagid the tag id
	 * @return the view
	 */
	@GET
	@Path(IFileTags.DELETE_TAG)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView deleteTagView(@PathParam(IWebPath.VAR_ID) Long fileid, @PathParam("tag" + IWebPath.VAR_ID) Long tagid);
	
	/**
	 * @param fileid the file id
	 * @param tags the tags
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IFileTags.TAGED_FILE)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer saveTaggedFile(@PathParam(IWebPath.VAR_ID) Long fileid, @FormParam("tags") Long[] tags) throws FormErrorException;
	
	/**
	 * @param fileid the file id
	 * @param name the tag name
	 * @param type the tag type
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IFileTags.ADD_TAG)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer saveNewTag(@PathParam(IWebPath.VAR_ID) Long fileid, @FormParam("name") String name, @FormParam("type") String type) throws FormErrorException;
	
	/**
	 * @param fileid the file id
	 * @param tagid the tag id
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IFileTags.DELETE_TAG)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteTag(@PathParam(IWebPath.VAR_ID) Long fileid, @PathParam("tag" + IWebPath.VAR_ID) Long tagid) throws FormErrorException;
}
