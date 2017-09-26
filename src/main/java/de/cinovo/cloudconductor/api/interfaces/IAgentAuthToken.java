package de.cinovo.cloudconductor.api.interfaces;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.AgentAuthToken;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Path("/authtoken")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IAgentAuthToken {
	
	/**
	 * @return all auth tokens
	 */
	@GET
	List<AgentAuthToken> getTokens();
	
	/**
	 * @return the generated token
	 */
	@PUT
	@Path("/generate")
	AgentAuthToken generateNewToken();
	
	/**
	 * Revoke agent auth token with given id.
	 * 
	 * @param tokenId the id of the token to be revoked
	 * @param comment optional text why token is revoked
	 */
	@PUT
	@Path("/{tokenId}/revoke")
	void revokeToken(@PathParam("tokenId") Long tokenId, String comment);
	
}
