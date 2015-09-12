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
import javax.ws.rs.QueryParam;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.RenderedUI;

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
	
	/***/
	public static final String ADD_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}/template" + IWebPath.ACTION_ADD;
	/***/
	public static final String ADD_KEY_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/key" + IWebPath.ACTION_ADD;
	
	/***/
	public static final String REMOVE_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}/template/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_DELETE;
	
	/***/
	public static final String DELETE_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	/***/
	public static final String SAVE_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_SAVE;
	/***/
	public static final String EDIT_KEY_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	
	
	/**
	 * @param viewtype the viewtype
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI view(@QueryParam("viewtype") String viewtype);
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addView();
	
	/**
	 * @param owner the owner
	 * @return the modal content
	 */
	@GET
	@Path(ISSHKey.ADD_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addTemplateView(@PathParam(IWebPath.VAR_NAME) String owner);
	
	/**
	 * @param template the template name
	 * @return the modal content
	 */
	@GET
	@Path(ISSHKey.ADD_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI addKeyView(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	/**
	 * @param owner the owner
	 * @param tname the template name
	 * @return the modal content
	 */
	@GET
	@Path(ISSHKey.REMOVE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteTemplateView(@PathParam(IWebPath.VAR_NAME) String owner, @PathParam(IWebPath.VAR_TEMPLATE) String tname);
	
	/**
	 * @param owner the owner
	 * @return the modal content
	 */
	@GET
	@Path(ISSHKey.DELETE_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI deleteView(@PathParam(IWebPath.VAR_NAME) String owner);
	
	/**
	 * @param owner the owner
	 * @return the modal content
	 */
	@GET
	@Path(ISSHKey.EDIT_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedUI editView(@PathParam(IWebPath.VAR_NAME) String owner);
	
	/**
	 * @param oldOwner the old owner name
	 * @param owner the new owner name
	 * @param key the ssh key
	 * @param templates associated tempalte names
	 * @return an ajax answer
	 * @throws FormErrorException one form errors
	 */
	@POST
	@Path(ISSHKey.SAVE_KEY_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer save(@PathParam(IWebPath.VAR_NAME) String oldOwner, @FormParam("owner") String owner, @FormParam("key_content") String key, @FormParam("templates") String[] templates) throws FormErrorException;
	
	/**
	 * @param owner the owner
	 * @param templates the template names
	 * @return an ajax answer
	 */
	@POST
	@Path(ISSHKey.ADD_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer addTemplate(@PathParam(IWebPath.VAR_NAME) String owner, @FormParam("templates") String[] templates);
	
	/**
	 * @param template the template name
	 * @param keys the keys
	 * @return an ajax answer
	 */
	@POST
	@Path(ISSHKey.ADD_KEY_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer addKey(@PathParam(IWebPath.VAR_TEMPLATE) String template, @FormParam("keys") String[] keys);
	
	/**
	 * @param owner the owner
	 * @param tname the template name
	 * @return an ajax answer
	 */
	@POST
	@Path(ISSHKey.REMOVE_TEMPLATE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer deleteTemplate(@PathParam(IWebPath.VAR_NAME) String owner, @PathParam(IWebPath.VAR_TEMPLATE) String tname);
	
	/**
	 * @param owner the owner
	 * @return an ajax answer
	 */
	@POST
	@Path(ISSHKey.DELETE_KEY_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer delete(@PathParam(IWebPath.VAR_NAME) String owner);
	
}
