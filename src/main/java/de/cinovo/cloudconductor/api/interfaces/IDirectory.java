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
import de.cinovo.cloudconductor.api.model.Directory;

import javax.ws.rs.*;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Path("/directory")
public interface IDirectory  {

	/**
	 * @return set of api objects
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Directory[] get();

	/**
	 * @param name the name of the object to save
	 * @param apiObject the api object
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void save(Directory apiObject);

	/**
	 * @param name the name of the api object
	 * @return the api object
	 */
	@GET
	@Path("/{directory}")
	@Produces(MediaType.APPLICATION_JSON)
	public Directory get(@PathParam("directory") String name);

	/**
	 * @param name the name of the api object
	 */
	@DELETE
	@Path("/{directory}")
	public void delete(@PathParam("directory") String name);
	
	/**
	 * @param template the template name
	 * @return the config files for the template
	 */
	@GET
	@Path("/template/{template}")
	@Produces(MediaType.APPLICATION_JSON)
	public Directory[] getDirectories(@PathParam("template") String template);
}
