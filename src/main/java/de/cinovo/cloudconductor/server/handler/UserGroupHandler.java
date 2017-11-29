package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.UserGroup;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class UserGroupHandler {

	@Autowired
	private IUserGroupDAO userGroupDAO;

	/**
	 * @param userGroup the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EUserGroup createEntity(UserGroup userGroup) throws WebApplicationException {
		EUserGroup group = new EUserGroup();
		group.setName(userGroup.getName());
		group.setPermissions(userGroup.getPermissions());
		return this.userGroupDAO.save(group);
	}

	/**
	 * @param eUserGroup the entity to update
	 * @param userGroup  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EUserGroup updateEntity(EUserGroup eUserGroup, UserGroup userGroup) throws WebApplicationException {
		eUserGroup.setDescription(userGroup.getDescription());
		eUserGroup.setPermissions(userGroup.getPermissions());
		return this.userGroupDAO.save(eUserGroup);
	}
}
