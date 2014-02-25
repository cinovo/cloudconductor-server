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

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.INamed;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 * @param <T> the api object
 */
public interface IDefaultApi<T extends INamed> {
	
	/**
	 * @return set of api objects
	 */
	@GET
	@Path(IRestPath.ROOT)
	@Produces(MediaType.APPLICATION_JSON)
	public T[] get();
	
	/**
	 * @param name the name of the object to save
	 * @param apiObject the api object
	 */
	@PUT
	@Path(IRestPath.DEFAULT_NAME)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void save(@PathParam(IRestPath.VAR_NAME) String name, T apiObject);
	
	/**
	 * @param name the name of the api object
	 * @return the api object
	 */
	@GET
	@Path(IRestPath.DEFAULT_NAME)
	@Produces(MediaType.APPLICATION_JSON)
	public T get(@PathParam(IRestPath.VAR_NAME) String name);
	
	/**
	 * @param name the name of the api object
	 */
	@DELETE
	@Path(IRestPath.DEFAULT_NAME)
	public void delete(@PathParam(IRestPath.VAR_NAME) String name);
}
