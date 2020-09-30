package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.dao.IUserDAO;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
		// language=HQL
		return this.findByQuery("FROM EUser u WHERE u.loginName = ?1", loginName);
	}

	@Override
	public boolean existsByLogin(String loginName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(u) FROM EUser AS u WHERE u.loginName = ?1", Long.class).setParameter(1, loginName).getSingleResult() > 0;
	}

	@Override
	public int deleteByLoginName(String loginName) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM EUser AS u WHERE u.loginName = ?1").setParameter(1, loginName).executeUpdate();
	}

	@Override
	public List<EUser> findByGroup(EUserGroup group) {
		// language=HQL
		return this.findListByQuery("FROM EUser AS u WHERE ?1 IN elements(u.userGroup)", group);
	}

	@Override
	public List<EUser> findByGroup(String groupName) {
		// language=HQL
		return this.findListByQuery("FROM EUser AS u JOIN FETCH u.userGroup AS g WHERE g.name = ?1", groupName);
	}

	@Override
	public Class<EUser> getEntityClass() {
		return EUser.class;
	}
	
}
