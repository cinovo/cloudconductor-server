package de.cinovo.cloudconductor.server.rest.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IUser;
import de.cinovo.cloudconductor.api.model.PasswordChange;
import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.handler.UserHandler;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.security.AuthHandler;
import de.cinovo.cloudconductor.server.security.TokenHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import de.taimos.dvalin.jaxrs.security.annotation.LoggedIn;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class UserImpl implements IUser {
	
	@Autowired
	private IUserDAO userDAO;
	
	@Autowired
	private UserHandler userHandler;
	@Autowired
	private TokenHandler tokenHandler;
	@Autowired
	private AuthHandler authHandler;
	
	
	@Override
	@Transactional
	public List<User> getUsers() {
		List<User> result = new ArrayList<>();
		for (EUser eUser : this.userDAO.findList()) {
			result.add(eUser.toApi());
		}
		return result;
	}
	
	@Override
	@Transactional
	public void save(User user) {
		RESTAssert.assertNotNull(user);
		RESTAssert.assertNotEmpty(user.getLoginName());
		EUser eUser = this.userDAO.findByLoginName(user.getLoginName());
		if (eUser == null) {
			this.userHandler.createEntity(user);
		} else {
			this.userHandler.updateEntity(user, eUser);
		}
	}
	
	@Override
	@Transactional
	public User getUser(String userName) {
		RESTAssert.assertNotEmpty(userName);
		EUser eUser = this.userDAO.findByLoginName(userName);
		RESTAssert.assertNotNull(eUser);
		return eUser.toApi();
	}
	
	@Override
	@Transactional
	public void delete(String userName) {
		RESTAssert.assertNotEmpty(userName);
		EUser eUser = this.userDAO.findByLoginName(userName);
		RESTAssert.assertNotNull(eUser);
		this.userDAO.delete(eUser);
	}
	
	@Override
	@Transactional
	public User createAuthToken(String userName) {
		RESTAssert.assertNotEmpty(userName);
		EUser eUser = this.userDAO.findByLoginName(userName);
		RESTAssert.assertNotNull(eUser);
		this.tokenHandler.generateAuthToken(eUser);
		eUser = this.userDAO.findByLoginName(userName);
		return eUser.toApi();
	}
	
	@Override
	@Transactional
	public User revokeAuthToken(String userName, String token) {
		RESTAssert.assertNotEmpty(userName);
		RESTAssert.assertNotEmpty(token);
		EUser eUser = this.userDAO.findByLoginName(userName);
		RESTAssert.assertNotNull(eUser);
		if (this.tokenHandler.revokeAuthToken(eUser, token)) {
			eUser = this.userDAO.findByLoginName(userName);
		}
		return eUser.toApi();
	}
	
	@Override
	@Transactional
	@LoggedIn
	public void changePassword(PasswordChange pwChange) {
		RESTAssert.assertNotNull(pwChange);
		RESTAssert.assertNotEmpty(pwChange.getUserName());
		RESTAssert.assertFalse(pwChange.getUserName().equalsIgnoreCase(AuthorizationProvider.ANONYMOUS_USER));
		RESTAssert.assertNotEmpty(pwChange.getNewPassword());
		RESTAssert.assertNotEmpty(pwChange.getOldPassword());
		RESTAssert.assertFalse(pwChange.getNewPassword().equals(pwChange.getOldPassword()));
		EUser currentUser = this.authHandler.getCurrentUser();
		RESTAssert.assertNotNull(currentUser);
		RESTAssert.assertTrue(pwChange.getUserName().equals(currentUser.getUsername()));
		EUser eUser = this.userDAO.findByLoginName(pwChange.getUserName());
		RESTAssert.assertNotNull(eUser);
		this.userHandler.changePassword(eUser, pwChange.getOldPassword(), pwChange.getNewPassword());
	}
	
}
