package de.cinovo.cloudconductor.server.security;

import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import de.taimos.dvalin.jaxrs.security.jwt.AuthenticatedUser;
import de.taimos.dvalin.jaxrs.security.jwt.JWTAuth;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class AuthProvider extends AuthorizationProvider {
	
	@Autowired
	private JWTAuth auth;
	@Autowired
	private IJWTTokenDAO jwtTokenDAO;
	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private IUserGroupDAO userGroupDAO;
	
	
	@Override
	protected boolean isAuthorizationMandatory() {
		return true;
	}
	
	@Override
	protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
		if ((auth == null) || auth.isEmpty()) {
			return null;
		}
		if (type.equalsIgnoreCase("bearer")) {
			return this.handleJWTAuth(msg, auth);
		}
		return null;
	}
	
	@Override
	protected SecurityContext handleOther(ContainerRequestContext requestContext, Message message, HttpHeaders httpHeaders) {
		Set<String> permissions = new HashSet<>();
		EUserGroup anonymous = this.userGroupDAO.findByName("Anonymous");
		if (anonymous != null) {
			permissions = anonymous.getPermissionsAsString();
		}
		return AuthorizationProvider.createAnonymousSC(permissions.toArray(new String[0]));
	}
	
	private SecurityContext handleJWTAuth(Message msg, String auth) {
		try {
			AuthenticatedUser authenticatedUser = this.auth.validateToken(auth);
			if (authenticatedUser == null) {
				return null;
			}
			EJWTToken jwtToken = this.jwtTokenDAO.findByToken(auth);
			if ((jwtToken == null) || !jwtToken.isActive()) {
				return null;
			}
			if ((jwtToken.getUserId() == null)) {
				return null;
			}
			EUser user = this.userDAO.findById(jwtToken.getUserId());
			if (user == null || !user.isActive()) {
				return null;
			}
			return this.loginUser(msg, new AuthenticatedUserWithToken(authenticatedUser, auth));
		} catch (ParseException e) {
			return null;
		}
	}
	
}
