package de.cinovo.cloudconductor.server.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
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
			// Convention: String has the following Form: "Authentication: Basic <Token>_<AgentName>"
			// auth1 is already reduced to: "<Token>_<AgentName>"
			String token = auth1.split("_")[0];
			String agentName = auth1.split("_")[1];
			
			EAgentAuthToken authToken = this.dToken.findByToken(token);
			if ((authToken != null) && (authToken.getRevoked() != null)) {
				authToken = null;
			}
			EAgent dbAgent = this.dAgent.findAgentByName(agentName);
			if ((authToken != null) && (dbAgent != null)) {
				return AuthorizationProvider.createSC(agentName);
			}
		}
		return null;
	}
	
	private SecurityContext noTokenNeededCheck() {
		if (!this.isAuthorizationMandatory()) {
			return AuthorizationProvider.createSC("Agent");
		}
		return null;
	}
}
