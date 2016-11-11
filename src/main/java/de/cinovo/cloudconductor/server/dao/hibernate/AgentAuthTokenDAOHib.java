package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Repository("AgentAuthTokenDAOHib")
public class AgentAuthTokenDAOHib extends EntityDAOHibernate<EAgentAuthToken, Long> implements IAgentAuthTokenDAO {
	
	@Override
	public Class<EAgentAuthToken> getEntityClass() {
		return EAgentAuthToken.class;
	}
	
	@Override
	public boolean isTokenUnique(String authToken) {
		List<EAgentAuthToken> allTokens = this.findListByQuery("FROM EAgentAuthToken a WHERE a.token = ?1", authToken);
		if (allTokens.size() > 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public EAgentAuthToken findByToken(String authToken) {
		EAgentAuthToken token = this.findByQuery("FROM EAgentAuthToken a WHERE a.token = ?1", authToken);
		return token;
	}
	
}
