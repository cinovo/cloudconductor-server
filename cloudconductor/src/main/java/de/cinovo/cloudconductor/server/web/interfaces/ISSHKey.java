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
@Path(ISSHKey.ROOT)
public interface ISSHKey {
	
	/** the root */
	public static final String ROOT = "/ssh";
	/** the main view sorted by Template */
	public static final String VIEW_TEMPLATE = "/byTemplate";
	
	public static final String ADD_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}/template" + IWebPath.ACTION_ADD;
	public static final String ADD_KEY_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/key" + IWebPath.ACTION_ADD;
	
	public static final String REMOVE_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}/template/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_REMOVE;
	
	public static final String DELETE_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	public static final String SAVE_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	public static final String EDIT_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@GET
	@Path(ISSHKey.VIEW_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewByTemplate();
	
	@GET
	@Path(ISSHKey.ADD_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddTemplate(@PathParam(IWebPath.VAR_NAME) String owner);
	
	@POST
	@Path(ISSHKey.ADD_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addTemplate(@PathParam(IWebPath.VAR_NAME) String owner, @FormParam("templates") String[] templates);
	
	@GET
	@Path(ISSHKey.ADD_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddKey(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@POST
	@Path(ISSHKey.ADD_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addKey(@PathParam(IWebPath.VAR_TEMPLATE) String template, @FormParam("keys") String[] keys);
	
	@GET
	@Path(ISSHKey.REMOVE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewRemoveTemplate(@PathParam(IWebPath.VAR_NAME) String owner, @PathParam(IWebPath.VAR_TEMPLATE) String tname);
	
	@POST
	@Path(ISSHKey.REMOVE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response removeTemplate(@PathParam(IWebPath.VAR_NAME) String owner, @PathParam(IWebPath.VAR_TEMPLATE) String tname);
	
	@GET
	@Path(ISSHKey.DELETE_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewDelete(@PathParam(IWebPath.VAR_NAME) String owner);
	
	@POST
	@Path(ISSHKey.DELETE_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_NAME) String owner);
	
	@GET
	@Path(ISSHKey.EDIT_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel edit(@PathParam(IWebPath.VAR_NAME) String owner);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel add();
	
	@POST
	@Path(IWebPath.ACTION_SAVE)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response save(@FormParam("owner") String fowner, @FormParam("key_content") String key, @FormParam("templates") String[] templates);
	
	@POST
	@Path(ISSHKey.SAVE_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response save(@PathParam(IWebPath.VAR_NAME) String owner, @FormParam("owner") String fowner, @FormParam("key_content") String key, @FormParam("templates") String[] templates);
	
}
