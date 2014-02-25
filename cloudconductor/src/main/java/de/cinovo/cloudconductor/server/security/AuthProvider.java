package de.cinovo.cloudconductor.server.security;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.security.Principal;

import javax.security.auth.Subject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class AuthProvider implements RequestHandler {
	
	private static final String PROP_USERNAME = "cloudconductor.username";
	private static final String PROP_PASSWORD = "cloudconductor.password";
	
	private static final String REALM = "Basic realm=\"cloudconductor\"";
	
	private static final String PRINCIPAL = "Admin";
	
	
	@Override
	public Response handleRequest(final Message m, final ClassResourceInfo resourceClass) {
		final AuthorizationPolicy policy = m.get(AuthorizationPolicy.class);
		if (policy != null) {
			if (System.getProperty(AuthProvider.PROP_USERNAME).equals(policy.getUserName()) && System.getProperty(AuthProvider.PROP_PASSWORD).equals(policy.getPassword())) {
				m.put(SecurityContext.class, AuthProvider.createSC());
				return null;
			}
		}
		
		throw new NotAuthorizedException(AuthProvider.getFaultResponse());
	}
	
	private static SecurityContext createSC() {
		final Subject subject = new Subject();
		final Principal principal = new SimplePrincipal(AuthProvider.PRINCIPAL);
		subject.getPrincipals().add(principal);
		return new DefaultSecurityContext(principal, subject);
	}
	
	private static Response getFaultResponse() {
		return Response.status(401).header(HttpHeaders.WWW_AUTHENTICATE, AuthProvider.REALM).build();
	}
	
}
