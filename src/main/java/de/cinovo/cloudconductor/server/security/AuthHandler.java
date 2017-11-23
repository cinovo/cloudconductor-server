package de.cinovo.cloudconductor.server.security;

import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jaxrs.context.DvalinRSContext;
import de.taimos.dvalin.jaxrs.security.IUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class AuthHandler {

	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private DvalinRSContext rsContext;
	@Autowired
	private IAuthTokenDAO authTokenDAO;

	/**
	 * @return the current logged in user
	 */
	public EUser getCurrentUser() {
		IUser currentUser = this.rsContext.getCurrentUser();
		if(currentUser == null) {
			return null;
		}
		return this.userDAO.findByLoginName(currentUser.getUsername());
	}

	/**
	 * @return the current authenticated user with his token
	 */
	public AuthenticatedUserWithToken getCurrentAuthenticatedUser() {
		IUser currentUser = this.rsContext.getCurrentUser();
		if(currentUser == null || !(currentUser instanceof AuthenticatedUserWithToken)) {
			return null;
		}
		return (AuthenticatedUserWithToken) currentUser;
	}

	/**
	 * @param username the username
	 * @param password the passwort
	 * @return the corresponding user
	 */
	public EUser getUser(String username, String password) {
		EUser user = this.userDAO.findByLoginName(username);
		if(user == null) {
			return null;
		}
		if(user.getPassword() == null || !user.getPassword().validate(password)) {
			return null;
		}
		return user;
	}

	/**
	 * @param token the authentication token
	 * @return the user
	 */
	public EUser getUser(String token) {
		EAuthToken authToken = this.authTokenDAO.findByToken(token);
		if(authToken == null || authToken.getUser() == null) {
			return null;
		}
		return authToken.getUser();
	}
}
