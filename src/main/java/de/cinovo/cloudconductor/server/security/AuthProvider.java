package de.cinovo.cloudconductor.server.security;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
public class AuthProvider extends AuthorizationProvider {
	
	private static final String PROP_USERNAME = "cloudconductor.username";
	private static final String PROP_PASSWORD = "cloudconductor.password";
	
	private static final String PRINCIPAL = "Admin";
	
	
	@Override
	protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
		final AuthorizationPolicy policy = msg.get(AuthorizationPolicy.class);
		if (policy != null) {
			if (System.getProperty(AuthProvider.PROP_USERNAME).equals(policy.getUserName()) && System.getProperty(AuthProvider.PROP_PASSWORD).equals(policy.getPassword())) {
				return AuthorizationProvider.createSC(AuthProvider.PRINCIPAL);
			}
		}
		return null;
	}
	
	@Override
	protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
		return null;
	}
	
	@Override
	protected boolean isAuthorizationMandatory() {
		return true;
	}
	
	@Override
	protected boolean sendWWWAuthenticate() {
		return true;
	}
	
}
