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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.Service;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.SERVICE)
public interface IService extends IDefaultApi<Service> {
	
	/**
	 * Get the packages associated with a service
	 * 
	 * @param service the service name
	 * @return collection of packages associated with the service
	 */
	@GET
	@Path(IRestPath.SERVICE_PKG)
	@Produces(MediaType.APPLICATION_JSON)
	public Package[] getPackages(@PathParam(IRestPath.VAR_SERVICE) String service);
	
	/**
	 * Add a package to the service
	 * 
	 * @param service the service name
	 * @param pkg the package name
	 */
	@PUT
	@Path(IRestPath.SERVICE_PKG_SINGLE)
	public void addPackage(@PathParam(IRestPath.VAR_SERVICE) String service, @PathParam(IRestPath.VAR_PKG) String pkg);
	
	/**
	 * Delete a package from the service
	 * 
	 * @param service the service name
	 * @param pkg the package name
	 */
	@DELETE
	@Path(IRestPath.SERVICE_PKG_SINGLE)
	public void removePackage(@PathParam(IRestPath.VAR_SERVICE) String service, @PathParam(IRestPath.VAR_PKG) String pkg);
}
