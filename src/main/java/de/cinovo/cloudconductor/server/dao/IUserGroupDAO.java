package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IUserGroupDAO extends IEntityDAO<EUserGroup, Long> {
	/**
	 * @param groupName the group name
	 * @return the group
	 */
	EUserGroup findByName(String groupName);
}
