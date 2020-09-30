package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("JWTTokenDAOHib")
public class JWTTokenDAOHib extends EntityDAOHibernate<EJWTToken, Long> implements IJWTTokenDAO {

	@Override
	public EJWTToken findByToken(String token) {
		if ((token == null) || token.isEmpty()) {
			return null;
		}
		// language=HQL
		return this.findByQuery("FROM EJWTToken AS j WHERE j.token = ?1", token);
	}

	@Override
	public int deleteByToken(String token) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.token = ?1").setParameter(1, token).executeUpdate();
	}

	@Override
	public List<EJWTToken> findByRefToken(EAuthToken refToken) {
		if ((refToken == null)) {
			return null;
		}
		return this.findListByQuery("FROM EJWTToken AS j WHERE j.refToken = ?1", refToken);
	}

	@Override
	public int deleteByRefToken(EAuthToken refToken) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.refToken = ?1").setParameter(1, refToken).executeUpdate();
	}

	@Override
	public int deleteByUser(EUser user) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.user = ?1").setParameter(1, user).executeUpdate();
	}

	@Override
	public Class<EJWTToken> getEntityClass() {
		return EJWTToken.class;
	}
}
