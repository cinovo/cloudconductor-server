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
@Path(IService.ROOT)
public interface IService {
	
	/** the root */
	public static final String ROOT = "/services";
	
	public static final String EDIT_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	public static final String ADD_PACKAGE_ACTION = "/{" + IWebPath.VAR_NAME + "}/package" + IWebPath.ACTION_ADD;
	public static final String REMOVE_PACKAGE_ACTION = "/{" + IWebPath.VAR_NAME + "}/package/{" + IWebPath.VAR_PKG + "}" + IWebPath.ACTION_REMOVE;
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAdd();
	
	@POST
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object add(@FormParam("servicename") String servicename, @FormParam("initscript") String initscript, @FormParam("description") String description, @FormParam("pkg") String[] pkgs);
	
	@GET
	@Path(IService.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewEdit(@PathParam(IWebPath.VAR_NAME) String sname);
	
	@POST
	@Path(IService.EDIT_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object edit(@PathParam(IWebPath.VAR_NAME) String sname, @FormParam("servicename") String servicename, @FormParam("initscript") String initscript, @FormParam("description") String description, @FormParam("pkg") String[] pkgs);
	
	@GET
	@Path(IService.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddPkg(@PathParam(IWebPath.VAR_NAME) String sname);
	
	@POST
	@Path(IService.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addPkg(@PathParam(IWebPath.VAR_NAME) String sname, @FormParam("pkg") String[] pkgs);
	
	@GET
	@Path(IService.REMOVE_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewRemovePkg(@PathParam(IWebPath.VAR_NAME) String sname, @PathParam(IWebPath.VAR_PKG) String pname);
	
	@POST
	@Path(IService.REMOVE_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response removePkg(@PathParam(IWebPath.VAR_NAME) String sname, @PathParam(IWebPath.VAR_PKG) String pname);
	
	@GET
	@Path(IService.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewDelete(@PathParam(IWebPath.VAR_NAME) String sname);
	
	@POST
	@Path(IService.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_NAME) String sname);
	
}
