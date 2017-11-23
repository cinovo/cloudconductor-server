package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
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
		List<EAuthToken> allTokens = this.findListByQuery("FROM EAuthToken a WHERE a.token = ?1", authToken);
		return allTokens.size() <= 0;
	}
	
	@Override
	public EAuthToken findByToken(String authToken) {
		EAuthToken token = this.findByQuery("FROM EAuthToken a WHERE a.token = ?1", authToken);
		return token;
	}
	
}
