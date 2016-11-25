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
	
	@Autowired
	private IAgentAuthTokenDAO dToken;
	@Autowired
	private IAgentDAO dAgent;
	
	
	@Override
	protected boolean isAuthorizationMandatory() {
		// if config token auth
		return true;
	}
	
	// TOKEN TOKE_AGENT
	@Override
	protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth1) {
		if (type == "TOKEN") {
			// Convention: String has the following Form: "Authentication: Basic <Token>_<AgentName>"
			// auth1 is already reduced to: "<Token>_<AgentName>"
			String token = auth1.split("_")[0];
			String agent = auth1.split("_")[1];
			
			EAgentAuthToken authToken = this.dToken.findByToken(token);
			EAgent dbAgent = this.dAgent.findAgentByName(agent);
			if ((authToken != null) && (dbAgent != null)) {
				return AuthorizationProvider.createSC(agent);
			}
		}
		return null;
	}
	
	@Override
	protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
		return null;
	}
	
}
