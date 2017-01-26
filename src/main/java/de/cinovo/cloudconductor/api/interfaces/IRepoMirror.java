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
import de.cinovo.cloudconductor.api.model.RepoMirror;

import javax.ws.rs.*;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path("/repomirror")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface IRepoMirror {
	
	/**
	 * @param id the id
	 * @return the mirror
	 */
	@GET
	@Path("/{id}")
	RepoMirror get(@PathParam("id") Long id);
	
	/**
	 * @param id the server id
	 */
	@DELETE
	@Path("/{id}")
	void delete(@PathParam("id") Long id);
	
	/**
	 * @param mirror the mirror
	 * @return the new id
	 */
	@POST
	@Path("/")
	Long newMirror(RepoMirror mirror);
	
	/**
	 * @param mirror the mirror
	 */
	@PUT
	@Path("/")
	void editMirror(RepoMirror mirror);
}
