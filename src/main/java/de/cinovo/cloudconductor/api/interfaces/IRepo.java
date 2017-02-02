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

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Repo;

import javax.ws.rs.*;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IRepo {

	/**
	 * @return set of api objects
	 */
	@GET
	Repo[] get();

	/**
	 * @param name the {@link Repo} name
	 * @return the {@link Repo}
	 */
	@GET
	@Path("/{name}")
	Repo get(@PathParam("name") String name);

	/**
	 * @param repo the {@link Repo}
	 * @return the new id
	 */
	@POST
	Long newRepo(Repo repo);

	/**
	 * @param repo the {@link Repo}
	 */
	@PUT
	void edit(Repo repo);

	/**
	 * @param name the name of the the {@link Repo} to delete
	 */
	@DELETE
	@Path("/{name}")
	void delete(@PathParam("name") String name);
}
