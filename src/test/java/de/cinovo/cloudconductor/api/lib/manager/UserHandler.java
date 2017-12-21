package de.cinovo.cloudconductor.api.lib.manager;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.PasswordChange;
import de.cinovo.cloudconductor.api.model.User;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@SuppressWarnings("javadoc")
public class UserHandler extends DefaultRestHandler<User> {
	
	public UserHandler(String cloudconductorUrl, String token) {
		super(cloudconductorUrl, token);
	}
	
	@Override
	protected String getDefaultPath() {
		return "/user";
	}
	
	@Override
	protected Class<User> getAPIClass() {
		return User.class;
	}
	
	public void changePassword(PasswordChange pwChange) throws CloudConductorException {
		this._put(this.getDefaultPath() + "/changepassword", pwChange);
	}
	
	public void createAuthToken(String username) throws CloudConductorException {
		String path = this.pathGenerator("/{username}/authtoken", username);
		this._put(path);
	}
	
	public void revokeAuthToken(String username, String token) throws CloudConductorException {
		String path = this.pathGenerator("/{username}/authtoken/{token}", username, token);
		this._delete(path);
	}
	
}
