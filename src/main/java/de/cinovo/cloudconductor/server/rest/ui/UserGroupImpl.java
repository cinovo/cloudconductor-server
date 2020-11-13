package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IUserGroup;
import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.api.model.UserGroup;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.handler.UserGroupHandler;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response.Status;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class UserGroupImpl implements IUserGroup {
	
	@Autowired
	private IUserGroupDAO userGroupDAO;
	@Autowired
	private IUserDAO userDAO;
	@Autowired
	private UserGroupHandler userGroupHandler;
	@Autowired
	private IAuthTokenDAO authTokenDAO;
	@Autowired
	private IAgentDAO agentDAO;
	
	@Override
	@Transactional
	public UserGroup[] getUserGroups() {
		return this.userGroupDAO.findList().stream().map(EUserGroup::toApi).toArray(UserGroup[]::new);
	}
	
	@Override
	@Transactional
	public void save(UserGroup userGroup) {
		RESTAssert.assertNotNull(userGroup);
		RESTAssert.assertNotEmpty(userGroup.getName());
		EUserGroup eUserGroup = this.userGroupDAO.findByName(userGroup.getName());
		if (eUserGroup == null) {
			this.userGroupHandler.createEntity(userGroup);
		} else {
			this.userGroupHandler.updateEntity(eUserGroup, userGroup);
		}
	}
	
	@Override
	@Transactional
	public UserGroup getUserGroup(String userGroupName) {
		RESTAssert.assertNotEmpty(userGroupName);
		EUserGroup eUserGroup = this.userGroupDAO.findByName(userGroupName);
		RESTAssert.assertNotNull(eUserGroup, Status.NOT_FOUND);
		return eUserGroup.toApi();
	}
	
	@Override
	@Transactional
	public void delete(String userGroupName) {
		RESTAssert.assertNotEmpty(userGroupName);
		RESTAssert.assertFalse(userGroupName.equalsIgnoreCase("anonymous"));
		EUserGroup userGroup = this.userGroupDAO.findByName(userGroupName);
		RESTAssert.assertNotNull(userGroup, Status.NOT_FOUND);
		// TODO check if group is still used
		this.userGroupDAO.delete(userGroup);
	}
	
	@Override
	@Transactional
	public User[] getGroupMembers(String userGroupName) {
		RESTAssert.assertNotEmpty(userGroupName);
		EUserGroup group = this.userGroupDAO.findByName(userGroupName);
		RESTAssert.assertNotNull(group);
		return this.userDAO.findByGroup(group).stream().map(u -> u.toApi(this.userGroupDAO, this.authTokenDAO, this.agentDAO)).toArray(User[]::new);
	}
	
}
