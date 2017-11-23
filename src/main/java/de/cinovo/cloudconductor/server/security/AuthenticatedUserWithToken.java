package de.cinovo.cloudconductor.server.security;

import de.taimos.dvalin.jaxrs.security.IUser;
import de.taimos.dvalin.jaxrs.security.jwt.AuthenticatedUser;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class AuthenticatedUserWithToken implements IUser {

	private AuthenticatedUser authenticatedUser;
	private String token;

	/**
	 * @param authenticatedUser the auth user
	 * @param token the token
	 */
	public AuthenticatedUserWithToken(AuthenticatedUser authenticatedUser, String token) {
		this.authenticatedUser = authenticatedUser;
		this.token = token;
	}

	/**
	 * @return the authenticatedUser
	 */
	public AuthenticatedUser getAuthenticatedUser() {
		return this.authenticatedUser;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}

	@Override
	public String getUsername() {
		return this.authenticatedUser.getUsername();
	}

	@Override
	public String[] getRoles() {
		return this.authenticatedUser.getRoles();
	}
}
