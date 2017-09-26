package de.cinovo.cloudconductor.server.handler;

import java.util.Date;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.security.AuthTokenGenerator;
import de.cinovo.cloudconductor.server.security.exception.TokenGenerationException;
import de.taimos.restutils.RESTAssert;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class AgentAuthTokenHandler {
	
	private static final int TOKEN_LENGTH = 32;
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentAuthTokenHandler.class);
	
	@Autowired
	private IAgentAuthTokenDAO tokenDAO;
	
	@Autowired
	private AuthTokenGenerator tokenGen;
	
	
	/**
	 * @return new agent auth token
	 */
	public EAgentAuthToken generateToken() {
		try {
			EAgentAuthToken newToken = this.tokenGen.generateAuthToken(AgentAuthTokenHandler.TOKEN_LENGTH);
			return newToken;
		} catch (TokenGenerationException e) {
			AgentAuthTokenHandler.LOGGER.error("Unable to generate new agent auth token: ", e);
			throw new WebApplicationException(e);
		}
	}
	
	/**
	 * Revokes agent auth token with given id.
	 * 
	 * @param tokenId the id of the token to revoke
	 * @param comment text why token was revoked
	 */
	public void revokeToken(Long tokenId, String comment) {
		EAgentAuthToken token = this.tokenDAO.findById(tokenId);
		
		RESTAssert.assertNotNull(token);
		token.setRevoked((new Date()).getTime());
		if ((comment != null) && (comment.length() > 0)) {
			token.setRevokeComment(comment);
		} else {
			token.setRevokeComment("");
		}
		
		this.tokenDAO.save(token);
	}
	
}
