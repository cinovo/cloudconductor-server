package de.cinovo.cloudconductor.api.interfaces;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

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

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.PackageServerGroup;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path("/packageservergroup")
public interface IPackageServerGroup {
	/**
	 * @return set of api objects
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public PackageServerGroup[] get();
	
	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public PackageServerGroup get(@PathParam("name") String name);
	
	@POST
	@Path("/")
	public void newGroup(PackageServerGroup group);
	
	@PUT
	@Path("/{id}")
	public void edit(@PathParam("id") Long id, PackageServerGroup group);
	
	@DELETE
	@Path("/{name}")
	public void delete(@PathParam("name") String name);
}
