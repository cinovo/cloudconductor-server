package de.cinovo.cloudconductor.api.interfaces;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.Service;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.HOST)
public interface IHost extends IDefaultApi<Host> {
	
	/**
	 * @param host the host name
	 * @return collection of services provided by the host
	 */
	@GET
	@Path(IRestPath.HOST_SERVICES)
	@Produces(MediaType.APPLICATION_JSON)
	public Service[] getServices(@PathParam(IRestPath.VAR_HOST) String host);
	
	/**
	 * Add a new service to the host or modify an existing service
	 * 
	 * @param host the host name
	 * @param name the service name
	 * @param service the service
	 * @return a response
	 */
	@PUT
	@Path(IRestPath.HOST_SERVICE_SVC)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setService(@PathParam(IRestPath.VAR_HOST) String host, @PathParam(IRestPath.VAR_SERVICE) String name, Service service);
	
	/**
	 * Delete the given service from the host
	 * 
	 * @param host the host name
	 * @param service the service name
	 * @return a response
	 */
	@DELETE
	@Path(IRestPath.HOST_SERVICE_SVC)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void removeService(@PathParam(IRestPath.VAR_HOST) String host, @PathParam(IRestPath.VAR_SERVICE) String service);
	
	/**
	 * Checks whether the host and template are in sync or not
	 * 
	 * @param host the host name
	 * @return whether host packages are in sync with template or not
	 */
	@GET
	@Path(IRestPath.HOST_SYNC)
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean inSync(@PathParam(IRestPath.VAR_HOST) String host);
	
	/**
	 * Starts a given service on a host, if the host supports the service
	 * 
	 * @param host the host name
	 * @param service the service name
	 * @return a response
	 */
	@PUT
	@Path(IRestPath.HOST_SERVICE_START)
	@Produces(MediaType.APPLICATION_JSON)
	public Response startService(@PathParam(IRestPath.VAR_HOST) String host, @PathParam(IRestPath.VAR_SERVICE) String service);
	
	/**
	 * Stops a given service on a host, if the host supports the service
	 * 
	 * @param host the host name
	 * @param service the service name
	 * @return a response
	 */
	@PUT
	@Path(IRestPath.HOST_SERVICE_STOP)
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopService(@PathParam(IRestPath.VAR_HOST) String host, @PathParam(IRestPath.VAR_SERVICE) String service);
	
	/**
	 * Restarts a given service on a host, if the host supports the service
	 * 
	 * @param host the host name
	 * @param service the service name
	 * @return a response
	 */
	@PUT
	@Path(IRestPath.HOST_SERVICE_RESTART)
	@Produces(MediaType.APPLICATION_JSON)
	public Response restartService(@PathParam(IRestPath.VAR_HOST) String host, @PathParam(IRestPath.VAR_SERVICE) String service);
	
}
