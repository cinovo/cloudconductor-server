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
	@Produces(MediaType.TEXT_HTML)
	public abstract Object save(@FormParam("name") String name, @FormParam("bgcolor") String bgcolor, @FormParam("allowautoupdate") String autoUpdate, @FormParam("description") String descr);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAdd();
	
	@POST
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object add(@FormParam("label") String label, @FormParam("link") String link);
	
	@GET
	@Path(IServerOptions.REMOVE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewRemove(@PathParam(IWebPath.VAR_NAME) String label);
	
	@POST
	@Path(IServerOptions.REMOVE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_NAME) String label);
	
}
