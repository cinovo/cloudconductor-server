package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jaxrs.security.HashedPassword;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class UserHandler {
	
	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private IJWTTokenDAO jwtDAO;
	@Autowired
	private IUserGroupDAO userGroupDAO;
	
	
	/**
	 * @param user the user to create
	 * @return the created user
	 */
	public EUser createEntity(User user) {
		EUser eUser = new EUser();
		eUser.setActive(user.isActive());
		eUser.setDisplayName(user.getDisplayName());
		eUser.setEmail(user.getEmail());
		eUser.setLoginName(user.getLoginName());
		if ((user.getPassword() != null) && !user.getPassword().isEmpty()) {
			eUser.setPassword(new HashedPassword(user.getPassword()));
		}
		eUser.setRegistrationDate(DateTime.now());
		for (String groupName : user.getUserGroups()) {
			EUserGroup group = this.userGroupDAO.findByName(groupName);
			if (group != null) {
				eUser.getUserGroup().add(group.getId());
			}
		}
		return this.userDAO.save(eUser);
	}
	
	/**
	 * @param user  the user
	 * @param eUser the entity
	 * @return the updated entity
	 */
	public EUser updateEntity(User user, EUser eUser) {
		eUser.setActive(user.isActive());
		eUser.setDisplayName(user.getDisplayName());
		eUser.setEmail(user.getEmail());
		eUser.setLoginName(user.getLoginName());
		eUser.setUserGroup(new HashSet<>());
		if ((user.getPassword() != null) && !user.getPassword().isEmpty()) {
			eUser.setPassword(new HashedPassword(user.getPassword()));
		}
		for (String groupName : user.getUserGroups()) {
			EUserGroup group = this.userGroupDAO.findByName(groupName);
			if (group != null) {
				eUser.getUserGroup().add(group.getId());
			}
		}
		return this.userDAO.save(eUser);
	}
	
	/**
	 * @param eUser       the user
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @return true if pw change was successful false if not
	 */
	public boolean changePassword(EUser eUser, String oldPassword, String newPassword) {
		if (!eUser.getPassword().validate(oldPassword)) {
			return false;
		}
		eUser.setPassword(new HashedPassword(newPassword));
		eUser = this.userDAO.save(eUser);
		this.jwtDAO.deleteByUser(eUser);
		return true;
	}
}
