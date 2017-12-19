package de.cinovo.cloudconductor.server.repo;

import de.cinovo.cloudconductor.api.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
@Path("/repos")
public interface IReposProvider {
	
	/**
	 * @param repo the repo name
	 * @param file the filename
	 * @return the response
	 */
	@Path("/{repo}/{file:.*}")
	@GET
	@Produces({MediaType.TEXT_HTML, javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM})
	Response get(@PathParam("repo") String repo, @PathParam("file") String file);
	
}
