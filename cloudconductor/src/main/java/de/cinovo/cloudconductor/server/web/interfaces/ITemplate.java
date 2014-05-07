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
@Path(ITemplate.ROOT)
public interface ITemplate {
	
	/** the root */
	public static final String ROOT = "/templates";
	
	public static final String CHANGE_STATE_ACTION = "/{" + IWebPath.VAR_NAME + "}";
	public static final String EDIT_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	public static final String ADD_PACKAGE_ACTION = "/{" + IWebPath.VAR_NAME + "}/package" + IWebPath.ACTION_ADD;
	public static final String DELETE_TEMPLATE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	public static final String DEFAULT_SERVICE_STATE = "/defaultservicestate";
	public static final String CHANGE_DEFAULT_SERVICE_STATE = "/defaultservicestate/{" + IWebPath.VAR_NAME + "}";
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	@POST
	@Path(ITemplate.CHANGE_STATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object changeTemplateState(@PathParam(IWebPath.VAR_NAME) String tname, @FormParam("update") String update, @FormParam("uninstall") String uninstall, @FormParam("updatePackage") String[] updatePackages, @FormParam("deletePackage") String[] deletePackages);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAddTemplate();
	
	@POST
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addTemplate(@FormParam("templatename") String templatename, @FormParam("yum") Long yum, @FormParam("description") String description, @FormParam("autoupdate") String autoupdate, @FormParam("smoothupdate") String smoothupdate);
	
	@GET
	@Path(ITemplate.EDIT_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewEditTemplate(@PathParam(IWebPath.VAR_NAME) String tname);
	
	@POST
	@Path(ITemplate.EDIT_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object editTemplate(@PathParam(IWebPath.VAR_NAME) String tname, @FormParam("templatename") String templatename, @FormParam("yum") Long yum, @FormParam("description") String description, @FormParam("autoupdate") String autoupdate, @FormParam("smoothupdate") String smoothupdate);
	
	@GET
	@Path(ITemplate.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel ViewAddPackage(@PathParam(IWebPath.VAR_NAME) String tname);
	
	@POST
	@Path(ITemplate.ADD_PACKAGE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object addPackage(@PathParam(IWebPath.VAR_NAME) String tname, @FormParam("pkg") String[] pkgs);
	
	@GET
	@Path(ITemplate.DELETE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewDeleteTemplate(@PathParam(IWebPath.VAR_NAME) String tname);
	
	@POST
	@Path(ITemplate.DELETE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object deleteTemplate(@PathParam(IWebPath.VAR_NAME) String tname);
	
	@GET
	@Path(ITemplate.DEFAULT_SERVICE_STATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewDefaultServiceStates();
	
	@POST
	@Path(ITemplate.CHANGE_DEFAULT_SERVICE_STATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response changeDefaultServiceStates(@PathParam(IWebPath.VAR_NAME) String tname, @FormParam("start") String[] start, @FormParam("stop") String[] stop, @FormParam("restart") String[] restart);
	
	@GET
	@Path("/toggleautorefresh")
	public Response handleAutorefresh();
	
}
