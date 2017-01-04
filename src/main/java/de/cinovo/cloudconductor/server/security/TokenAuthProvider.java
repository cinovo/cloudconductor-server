package de.cinovo.cloudconductor.server.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.taimos.springcxfdaemon.providers.AuthorizationProvider;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public class TokenAuthProvider extends AuthorizationProvider {
	
	private static final String TOKEN = "TOKEN";
	private static final Boolean TOKEN_AUTH = Boolean.valueOf(System.getProperty("cloudconductor.restauthmandatory", "true"));
	
	@Autowired
	private IAgentAuthTokenDAO dToken;
	@Autowired
	private IAgentDAO dAgent;
	
	
	@Override
	protected boolean isAuthorizationMandatory() {
		return TokenAuthProvider.TOKEN_AUTH;
	}
	
	@Override
	protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth1) {
		SecurityContext result = this.noTokenNeededCheck();
		if (result != null) {
			return result;
		}
		return this.doTokenAuth(type, auth1);
	}
	
	@Override
	protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
		return this.noTokenNeededCheck();
	}
	
	/**
	 * This Method allows login for tokens following the Convention for a Header: "Authentication: TOKEN 'Token'_'AgentName'"
	 */
	private SecurityContext doTokenAuth(String type, String auth1) {
		if (type.equals(TokenAuthProvider.TOKEN)) {
			// Convention: String has the following Form: "Authentication: TOKEN <Token>_<AgentName>"
			// auth1 is already reduced to: "<Token>_<AgentName>"
			String token = auth1.split("_")[0];
			String agentName = auth1.split("_")[1];
			
			EAgentAuthToken authToken = this.dToken.findByToken(token);
			if (authToken == null) {
				this.handleUnknownToken(token, agentName);
				return null;
			}
			
			if ((authToken.getRevoked() != null)) {
				this.handleAgent(agentName, authToken);
				return null;
			}
			
			EAgent agent = this.handleAgent(agentName, authToken);
			return AuthorizationProvider.createSC(agent.getName());
		}
		return null;
	}
	
	private void handleUnknownToken(String token, String agentName) {
		EAgentAuthToken brokenToken = new EAgentAuthToken();
		brokenToken.setToken(token);
		brokenToken.setCreationDate(DateTime.now().getMillis());
		brokenToken.setRevoked(DateTime.now().getMillis());
		brokenToken.setRevokeComment("Unknown token tried to beeing used by a host.");
		brokenToken = this.dToken.save(brokenToken);
		this.handleAgent(agentName, brokenToken);
	}
	
	private EAgent handleAgent(String agentName, EAgentAuthToken authToken) {
		EAgent dbAgent = this.dAgent.findAgentByName(agentName);
		if (dbAgent == null) {
			dbAgent = this.createNewAgent(agentName, authToken);
		}
		if ((dbAgent.getToken() == null) || !(dbAgent.getToken().equals(authToken))) {
			dbAgent.setToken(authToken);
			dbAgent.setTokenAssociationDate(DateTime.now().getMillis());
			dbAgent = this.dAgent.save(dbAgent);
		}
		return dbAgent;
	}
	
	private EAgent createNewAgent(String agentName, EAgentAuthToken authToken) {
		EAgent agent = new EAgent();
		agent.setName(agentName);
		agent.setToken(authToken);
		agent.setTokenAssociationDate(DateTime.now().getMillis());
		return this.dAgent.save(agent);
	}
	
	private SecurityContext noTokenNeededCheck() {
		if (!this.isAuthorizationMandatory()) {
			return AuthorizationProvider.createSC("Agent");
		}
		return null;
	}
}
