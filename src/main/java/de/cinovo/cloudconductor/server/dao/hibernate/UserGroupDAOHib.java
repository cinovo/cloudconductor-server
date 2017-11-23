package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */

@Repository("UserGroupDAOHib")
public class UserGroupDAOHib extends EntityDAOHibernate<EUserGroup, Long> implements IUserGroupDAO {
	@Override
	public EUserGroup findByName(String groupName) {
		if ((groupName == null) || groupName.isEmpty()) {
			return null;
		}
		return this.findByQuery("FROM EUserGroup u WHERE u.name = ?1", groupName);
	}

	@Override
	public Class<EUserGroup> getEntityClass() {
		return EUserGroup.class;
	}
}
