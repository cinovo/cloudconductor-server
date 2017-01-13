package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
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
@Repository("AgentAuthTokenDAOHib")
public class AgentAuthTokenDAOHib extends EntityDAOHibernate<EAgentAuthToken, Long> implements IAgentAuthTokenDAO {
	
	@Override
	public Class<EAgentAuthToken> getEntityClass() {
		return EAgentAuthToken.class;
	}
	
	@Override
	public boolean isTokenUnique(String authToken) {
		if ((authToken == null) || authToken.isEmpty()) {
			return false;
		}
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
