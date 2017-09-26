package de.cinovo.cloudconductor.server.rest.ui;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IAgentAuthToken;
import de.cinovo.cloudconductor.api.model.AgentAuthToken;
import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.handler.AgentAuthTokenHandler;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class AgentAuthTokenImpl implements IAgentAuthToken {
	
	@Autowired
	private IAgentAuthTokenDAO tokenDAO;
	
	@Autowired
	private AgentAuthTokenHandler tokenHandler;
	
	
	@Override
	@Transactional
	public List<AgentAuthToken> getTokens() {
		List<AgentAuthToken> tokens = new ArrayList<>();
		for (EAgentAuthToken token : this.tokenDAO.findList()) {
			tokens.add(token.toApi());
		}
		return tokens;
	}
	
	@Override
	@Transactional
	public AgentAuthToken generateNewToken() {
		EAgentAuthToken newToken = this.tokenHandler.generateToken();
		RESTAssert.assertNotNull(newToken);
		return newToken.toApi();
	}
	
	@Override
	@Transactional
	public void revokeToken(Long tokenId, String comment) {
		RESTAssert.assertNotNull(tokenId);
		this.tokenHandler.revokeToken(tokenId, comment);
	}
}
