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
@Path(IHost.ROOT)
public interface IHost {
	
	/** the root */
	public static final String ROOT = "/hosts";
	
	/***/
	public static final String SINGLE_HOST = "/{" + IWebPath.VAR_NAME + "}";
	/***/
	public static final String SERVICE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_UPDATE;
	/***/
	public static final String DELETE_ACTION = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_DELETE;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI view();
	
	/**
	 * @param hname the host name
	 * @return the view
	 */
	@GET
	@Path(IHost.SINGLE_HOST)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI view(@PathParam(IWebPath.VAR_NAME) String hname);
	
	/**
	 * @param hname the host name
	 * @param start services to start
	 * @param stop services to stop
	 * @param restart services to restart
	 * @return an ajax answer
	 */
	@POST
	@Path(IHost.SERVICE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer changeServiceStates(@PathParam(IWebPath.VAR_NAME) String hname, @FormParam("startService") String[] start, @FormParam("stopService") String[] stop, @FormParam("restartService") String[] restart);
	
	/**
	 * @param hname the host name
	 * @return an ajax answer
	 */
	@GET
	@Path(IHost.DELETE_ACTION)
	@Produces(MediaType.TEXT_HTML)
	public RenderedUI deleteHostView(@PathParam(IWebPath.VAR_NAME) String hname);
	
	/**
	 * @param hname the host name
	 * @return an ajax answer
	 * @throws FormErrorException on form errors
	 */
	@POST
	@Path(IHost.DELETE_ACTION)
	@Produces(MediaType.APPLICATION_JSON)
	public AjaxAnswer deleteHost(@PathParam(IWebPath.VAR_NAME) String hname) throws FormErrorException;
	
}
