package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

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
public class AgentAuthTokenDAOHib extends EntityDAOHibernate<EAgentAuthToken, Long> implements IAgentAuthTokenDAO {
	
	@Override
	public Class<EAgentAuthToken> getEntityClass() {
		return EAgentAuthToken.class;
	}
	
	@Override
	public boolean isTokenUnique(String authToken) {
		List<EAgentAuthToken> allTokens = this.findListByQuery("FROM EAuthToken a WHERE a.token = ?1", authToken);
		if (allTokens.size() > 0) {
			return false;
		}
		return true;
	}
	
}
