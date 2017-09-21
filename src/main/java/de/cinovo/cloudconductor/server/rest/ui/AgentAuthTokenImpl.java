package de.cinovo.cloudconductor.server.rest.ui;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IAgentAuthToken;
import de.cinovo.cloudconductor.api.model.AgentAuthToken;
import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.security.AuthTokenGenerator;
import de.cinovo.cloudconductor.server.security.exception.TokenGenerationException;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class AgentAuthTokenImpl implements IAgentAuthToken {
	
	private static final int TOKEN_LENGTH = 32;
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentAuthTokenImpl.class);
	
	@Autowired
	private IAgentAuthTokenDAO tokenDAO;
	@Autowired
	private AuthTokenGenerator tokenGen;
	
	
	@Override
	public List<AgentAuthToken> getTokens() {
		List<AgentAuthToken> tokens = new ArrayList<>();
		for (EAgentAuthToken token : this.tokenDAO.findList()) {
			tokens.add(token.toApi());
		}
		return tokens;
	}
	
	@Override
	public AgentAuthToken generateNewToken() {
		try {
			EAgentAuthToken newToken = this.tokenGen.generateAuthToken(AgentAuthTokenImpl.TOKEN_LENGTH);
			return newToken.toApi();
		} catch (TokenGenerationException e) {
			AgentAuthTokenImpl.LOGGER.error("Unable to generate new agent auth token: ", e);
			throw new WebApplicationException(e);
		}
	}
}
