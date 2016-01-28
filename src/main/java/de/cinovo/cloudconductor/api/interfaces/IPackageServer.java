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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.PackageServer;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path("/packageserver")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IPackageServer {
	
	/**
	 * @param id the id
	 * @return the package server
	 */
	@GET
	@Path("/{id}")
	public PackageServer get(@PathParam("id") Long id);
	
	/**
	 * @param id the server id
	 */
	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") Long id);
	
	/**
	 * @param ps the package server
	 * @return the new id
	 */
	@POST
	@Path("/")
	public Long newServer(PackageServer ps);
	
	/**
	 * @param ps the package server
	 */
	@PUT
	@Path("/")
	public void editServer(PackageServer ps);
}
