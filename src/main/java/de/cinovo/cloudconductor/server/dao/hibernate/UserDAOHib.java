package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */

@Repository("UserDAOHib")
public class UserDAOHib extends EntityDAOHibernate<EUser, Long> implements IUserDAO {
	
	@Override
	public EUser findByLoginName(String loginName) {
		if ((loginName == null) || loginName.isEmpty()) {
			return null;
		}
		return this.findByQuery("FROM EUser u WHERE u.loginName = ?1", loginName);
	}
	
	@Override
	public List<EUser> findByGroup(EUserGroup group) {
		return this.findListByQuery("FROM EUser u WHERE ?1 IN elements(u.userGroup)", group);
	}
	
	@Override
	public Class<EUser> getEntityClass() {
		return EUser.class;
	}
	
}
