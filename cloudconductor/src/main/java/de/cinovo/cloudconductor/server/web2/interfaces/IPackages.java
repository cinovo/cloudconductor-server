package de.cinovo.cloudconductor.server.web2.interfaces;

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

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
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
	/** */
	public static final String ADD_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services" + IWebPath.ACTION_ADD;
	/** */
	public static final String NEW_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services" + IWebPath.ACTION_NEW;
	/** */
	public static final String REMOVE_SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}/services/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_REMOVE;
	/** */
	public static final String ADD_PACKAGE_ACTION = "/{" + IWebPath.VAR_NAME + "}/version/{" + IWebPath.VAR_VERSION + "}" + IWebPath.ACTION_INSTALL;
	
	
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
	public abstract ViewModel addServiceView(@PathParam(IWebPath.VAR_NAME) String pname);
	
	@POST
	@Path(IPackages.ADD_SERVICE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect addService(@PathParam(IWebPath.VAR_NAME) String pname, @FormParam("services") String[] services) throws FormErrorException;
	
	@GET
	@Path(IPackages.NEW_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel newServiceView(@PathParam(IWebPath.VAR_NAME) String pname);
	
	@POST
	@Path(IPackages.NEW_SERVICE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect newService(@PathParam(IWebPath.VAR_NAME) String pname, @FormParam("servicename") String servicename, @FormParam("initscript") String initscript, @FormParam("description") String description) throws FormErrorException;
	
	@GET
	@Path(IPackages.REMOVE_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel deleteServiceView(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_SERVICE) String sname);
	
	@POST
	@Path(IPackages.REMOVE_SERVICE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect deleteService(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_SERVICE) String sname);
	
	/**
	 * @param pname the package name
	 * @param versionId the version id
	 * @return the view for adding package version to a template
	 */
	@GET
	@Path(IPackages.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel addPackageView(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_VERSION) Long versionId);
	
	/**
	 * @param pname the package name
	 * @param versionId the package version id
	 * @param templates the selected template names
	 * @return a redirect to the package page
	 * @throws FormErrorException error in case of wrong input
	 */
	@POST
	@Path(IPackages.ADD_PACKAGE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxRedirect addPackage(@PathParam(IWebPath.VAR_NAME) String pname, @PathParam(IWebPath.VAR_VERSION) Long versionId, @FormParam("templates") String[] templates) throws FormErrorException;
	
}
