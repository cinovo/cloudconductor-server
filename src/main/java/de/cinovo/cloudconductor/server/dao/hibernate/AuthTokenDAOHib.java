package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author ablehm
 */
@Repository("AuthTokenDAOHib")
public class AuthTokenDAOHib extends EntityDAOHibernate<EAuthToken, Long> implements IAuthTokenDAO {
	
	@Override
	public Class<EAuthToken> getEntityClass() {
		return EAuthToken.class;
	}
	
	@Override
	public boolean isTokenUnique(String authToken) {
		if ((authToken == null) || authToken.isEmpty()) {
			return false;
		}
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(t) FROM EAuthToken AS t WHERE t.token = ?1", Long.class).setParameter(1, authToken).getSingleResult() == 0;
	}
	
	@Override
	public EAuthToken findByToken(String authToken) {
		// language=HQL
		return this.findByQuery("FROM EAuthToken AS t WHERE t.token = ?1", authToken);
	}
	
	@Override
	public EAuthToken findByUserAndToken(EUser user, String token) {
		// language=HQL
		return this.findByQuery("FROM EAuthToken AS t WHERE t.userid = ?1 AND t.token =?2", user.getId(), token);
	}
	
	@Override
	public List<EAuthToken> findByUser(Long id) {
		// language=HQL
		return this.findListByQuery("FROM EAuthToken AS t WHERE t.userid = ?1", id);
	}
}
