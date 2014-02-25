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
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IPackages.ROOT)
public interface IPackages {
	
	/** the root */
	public static final String ROOT = "/packages";
	
	public static final String ADD_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services" + IWebPath.ACTION_ADD;
	public static final String ADD_NEW_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services" + IWebPath.ACTION_ADD + "/new";
	
	public static final String REMOVE_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_REMOVE;
	
	public static final String ADD_PACKAGE_ACTION = "/{" + IWebPath.VAR_NAME + "}/package/{" + IWebPath.VAR_VERSION + "}" + IWebPath.ACTION_ADD;
	
	
	/**
	 * Return a page showing all packages, their versions, and their services.
	 * 
	 * @return the page showing all packages
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@GET
	@Path(IPackages.ADD_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddService(@PathParam(IWebPath.VAR_NAME) String pname);
	
	@POST
	@Path(IPackages.ADD_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addService(@PathParam(IWebPath.VAR_NAME) String pname, @FormParam("service") String[] services);
	
	@POST
	@Path(IPackages.ADD_NEW_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addService(@PathParam(IWebPath.VAR_NAME) String pname, @FormParam("servicename") String servicename, @FormParam("initscript") String initscript, @FormParam("description") String description);
	
	@GET
	@Path(IPackages.REMOVE_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewDeleteService(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_SERVICE) String sname);
	
	@POST
	@Path(IPackages.REMOVE_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response deleteService(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_SERVICE) String sname);
	
	@GET
	@Path(IPackages.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddPackage(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_VERSION) Long rpmid);
	
	@POST
	@Path(IPackages.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response addPackage(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_VERSION) Long rpmid, @FormParam("templates") String[] templates);
	
}
