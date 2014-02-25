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

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.MediaType;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IPackageServer.ROOT)
public interface IPackageServer {
	
	/** the root */
	public static final String ROOT = "/pkgsrv";
	
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_DELETE;
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_EDIT;
	public static final String SAVE_ACTION = "/{" + IWebPath.VAR_ID + "}" + IWebPath.ACTION_SAVE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel view();
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel viewAdd();
	
	@GET
	@Path(IPackageServer.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel viewEdit(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	@GET
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract ViewModel viewDelete(@PathParam(IWebPath.VAR_ID) Long serverid);
	
	@POST
	@Path(IPackageServer.SAVE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract Object save(@PathParam(IWebPath.VAR_ID) Long serverid, @FormParam("path") String path, @FormParam("description") String description);
	
	@POST
	@Path(IPackageServer.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract Response delete(@PathParam(IWebPath.VAR_ID) Long serverid);
	
}
