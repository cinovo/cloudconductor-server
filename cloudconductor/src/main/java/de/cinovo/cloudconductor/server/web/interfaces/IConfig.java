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
@Path(IConfig.ROOT)
public interface IConfig {
	
	/** the root */
	public static final String ROOT = "/config";
	/** GLOBAL */
	public static final String RESERVED_GLOBAL = "GLOBAL";
	/** the main view sorted by Template */
	public static final String VIEW_TEMPLATE = "/{" + IWebPath.VAR_TEMPLATE + "}";
	
	/** add template */
	public static final String ADD_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_ADD;
	/** add service */
	public static final String ADD_SERVICE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}" + IWebPath.ACTION_ADD;
	
	/** edit template */
	public static final String EDIT_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_EDIT;
	/** edit service */
	public static final String EDIT_SERVICE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_EDIT;
	
	/** delete template */
	public static final String DELETE_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}" + IWebPath.ACTION_DELETE;
	/** delete service */
	public static final String DELETE_SERVICE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_DELETE;
	/** delete key */
	public static final String DELETE_KEY_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_DELETE;
	
	/** save service */
	public static final String SAVE_SERVICE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_SERVICE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_SAVE;
	/** save template */
	public static final String SAVE_TEMPLATE_ACTION = "/{" + IWebPath.VAR_TEMPLATE + "}/{" + IWebPath.VAR_KEY + "}" + IWebPath.ACTION_SAVE;
	
	/** batch mode */
	public static final String BATCH_ACTION = "/batchmod";
	
	
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response view();
	
	@GET
	@Path(IConfig.VIEW_TEMPLATE)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAdd();
	
	@GET
	@Path(IConfig.ADD_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAdd(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IConfig.ADD_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewAdd(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service);
	
	@GET
	@Path(IConfig.EDIT_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewEdit(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service, @PathParam(IWebPath.VAR_KEY) String key);
	
	@GET
	@Path(IConfig.EDIT_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewEdit(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_KEY) String key);
	
	@GET
	@Path(IConfig.DELETE_KEY_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_KEY) String key);
	
	@GET
	@Path(IConfig.DELETE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_TEMPLATE) String template);
	
	@GET
	@Path(IConfig.DELETE_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response delete(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service, @PathParam(IWebPath.VAR_KEY) String key);
	
	@POST
	@Path(IConfig.SAVE_SERVICE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object save(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_SERVICE) String service, @PathParam(IWebPath.VAR_KEY) String key, @FormParam("ftemplate") String ftemplate, @FormParam("fservice") String fservice, @FormParam("fkey") String fkey, @FormParam("fvalue") String fvalue);
	
	@POST
	@Path(IConfig.SAVE_TEMPLATE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object save(@PathParam(IWebPath.VAR_TEMPLATE) String template, @PathParam(IWebPath.VAR_KEY) String key, @FormParam("ftemplate") String ftemplate, @FormParam("fservice") String fservice, @FormParam("fkey") String fkey, @FormParam("fvalue") String fvalue);
	
	@POST
	@Path(IWebPath.ACTION_SAVE)
	@Produces(MediaType.TEXT_HTML)
	public abstract Object save(@FormParam("ftemplate") String ftemplate, @FormParam("fservice") String fservice, @FormParam("fkey") String fkey, @FormParam("fvalue") String fvalue);
	
	@GET
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel viewRestore();
	
	@POST
	@Path(IConfig.BATCH_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public abstract Response restore(@FormParam("restore") String restore);
	
}
