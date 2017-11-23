package de.cinovo.cloudconductor.server.rest.shared;

import de.cinovo.cloudconductor.api.interfaces.IAuth;
import de.cinovo.cloudconductor.api.model.Authentication;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.enums.AuthType;
import de.cinovo.cloudconductor.server.security.AuthHandler;
import de.cinovo.cloudconductor.server.security.AuthenticatedUserWithToken;
import de.cinovo.cloudconductor.server.security.TokenHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class AuthImpl implements IAuth {

	@Autowired
	private AuthHandler authHandler;
	@Autowired
	private TokenHandler tokenHandler;

	@Override
	public String auth(Authentication auth) {
		RESTAssert.assertNotNull(auth);
		if(auth.getToken() == null || auth.getToken().isEmpty()) {
			RESTAssert.assertNotEmpty(auth.getUsername());
			RESTAssert.assertNotEmpty(auth.getPassword());
			EUser user = this.authHandler.getUser(auth.getUsername(), auth.getPassword());
			return this.getToken(user, AuthType.PERSON, null);
		}
		RESTAssert.assertNotEmpty(auth.getToken());
		EUser user = this.authHandler.getUser(auth.getToken());
		return this.getToken(user, AuthType.AGENT, auth.getToken());
	}

	@Override
	public void logout() {
		AuthenticatedUserWithToken currentAuthenticatedUser = this.authHandler.getCurrentAuthenticatedUser();
		if(currentAuthenticatedUser != null) {
			this.tokenHandler.revokeJWTToken(currentAuthenticatedUser.getToken());
		} else {
			EUser currentUser = this.authHandler.getCurrentUser();
			this.tokenHandler.revokeJWTTokens(currentUser);
		}
	}

	private String getToken(EUser user, AuthType type, String refToken) {
		if(user == null) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		EJWTToken ejwtToken = this.tokenHandler.generateJWTToken(user, type, refToken);
		if(ejwtToken == null) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		return ejwtToken.getToken();
	}
}
