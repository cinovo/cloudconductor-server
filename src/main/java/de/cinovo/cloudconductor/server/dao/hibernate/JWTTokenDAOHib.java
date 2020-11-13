package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

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
	public void deleteByToken(String token) {
		// language=HQL
		this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.token = ?1").setParameter(1, token).executeUpdate();
	}
	
	
	@Override
	public void deleteByRefToken(EAuthToken refToken) {
		// language=HQL
		this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.refToken = ?1").setParameter(1, refToken.getId()).executeUpdate();
	}
	
	@Override
	public void deleteByUser(EUser user) {
		// language=HQL
		this.entityManager.createQuery("DELETE FROM EJWTToken AS j WHERE j.userId = ?1").setParameter(1, user.getId()).executeUpdate();
	}
	
	@Override
	public Class<EJWTToken> getEntityClass() {
		return EJWTToken.class;
	}
}
