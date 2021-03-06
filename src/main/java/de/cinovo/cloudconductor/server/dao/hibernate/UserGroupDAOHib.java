package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
		// language=HQL
		return this.findByQuery("FROM EUserGroup AS g WHERE g.name = ?1", groupName);
	}
	
	@Override
	public boolean exists(String groupName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(g) FROM EUserGroup AS g WHERE g.name = ?1", Long.class).setParameter(1, groupName).getSingleResult() > 0;
	}
	
	@Override
	public Class<EUserGroup> getEntityClass() {
		return EUserGroup.class;
	}
	
	@Override
	public List<EUserGroup> findByIds(Iterable<Long> userGroup) {
		if (userGroup == null || !userGroup.iterator().hasNext()) {
			return new ArrayList<>();
		}
		// language=HQL
		String q = "FROM EUserGroup AS ug WHERE ug.id IN ?1";
		return this.findListByQuery(q, userGroup);
	}
}
