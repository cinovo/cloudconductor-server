package de.cinovo.cloudconductor.server.rest.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IUserGroup;
import de.cinovo.cloudconductor.api.model.UserGroup;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.handler.UserGroupHandler;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

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
	private UserGroupHandler userGroupHandler;
	
	
	@Override
	public List<UserGroup> getUserGroups() {
		List<UserGroup> result = new ArrayList<>();
		for (EUserGroup group : this.userGroupDAO.findList()) {
			result.add(group.toApi());
		}
		return result;
	}
	
	@Override
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
	public void delete(String userGroupName) {
		RESTAssert.assertNotEmpty(userGroupName);
		RESTAssert.assertFalse(userGroupName.equalsIgnoreCase("anonymous"));
		EUserGroup userGroup = this.userGroupDAO.findByName(userGroupName);
		if (userGroup != null) {
			this.userGroupDAO.delete(userGroup);
		}
	}
}
