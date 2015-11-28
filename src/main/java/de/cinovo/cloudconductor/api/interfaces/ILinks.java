package de.cinovo.cloudconductor.api.interfaces;

import java.util.List;

import javax.ws.rs.Consumes;
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
 * http://www.apache.org/licenses/LICENSE-2.0
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
import javax.ws.rs.core.Response;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.AdditionalLink;

/**
 * 
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
@Path("/links")
@Produces(MediaType.APPLICATION_JSON)
public interface ILinks {
	
	@GET
	List<AdditionalLink> getLinks();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	Response createLink(AdditionalLink link);
	
	@Path("/{id}")
	@GET
	AdditionalLink getLink(@PathParam("id") long id);
	
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	AdditionalLink updateLink(@PathParam("id") long id, AdditionalLink link);
	
	@Path("/{id}")
	@DELETE
	void deleteLink(@PathParam("id") long id);
	
}
