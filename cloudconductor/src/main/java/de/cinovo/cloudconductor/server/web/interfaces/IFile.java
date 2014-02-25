package de.cinovo.cloudconductor.server.web.interfaces;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.cinovo.cloudconductor.api.MediaType;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IFile.ROOT)
public interface IFile {
	
	/** the root */
	public static final String ROOT = "/files";
	/** the main view sorted by Template */
	public static final String VIEW_TEMPLATE = "/byTemplate";
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	
	public static final String ADD_TO_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	public static final String REMOVE_FROM_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_REMOVE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@GET
	@Path(IFile.VIEW_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewByTemplate();
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddFile();
	
	@GET
	@Path(IFile.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewEditFile(@PathParam(IWebPath.VAR_NAME) String cfname);
	
	@GET
	@Path(IFile.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object deleteFile(@PathParam(IWebPath.VAR_NAME) String cfname);
	
	@POST
	@Path(IFile.SAVE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object saveFile(@PathParam(IWebPath.VAR_NAME) String cfname, @FormParam("name") String name, @FormParam("owner") String owner, @FormParam("group") String group, @FormParam("mode") String mode, @FormParam("targetPath") String targetPath, @FormParam("file_content") String content, @FormParam("isTemplate") Boolean isTemplate, @FormParam("depPackage") String depPackage, @FormParam("depServices") String[] depServices, @FormParam("templates") String[] templates);
	
	@GET
	@Path(IFile.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddFileToTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IFile.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewRemoveFileFromTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_NAME) String fname);
	
	@POST
	@Path(IFile.ADD_TO_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addFileToTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template, @FormParam("files") String[] files);
	
	@POST
	@Path(IFile.REMOVE_FROM_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response removeFileFromTemplate(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_NAME) String fname);
	
}
