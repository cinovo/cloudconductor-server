package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Repository("AgentDAOHib")
public class AgentDAOHib extends EntityDAOHibernate<EAgent, Long> implements IAgentDAO {
	
	@Override
	public Class<EAgent> getEntityClass() {
		return EAgent.class;
	}
	
	@Override
	public List<EAgent> getAgentsByToken(String authToken) {
		List<EAgent> agentsByToken = this.findListByQuery("FROM EAgent a WHERE a.token.token = ?1", authToken);
		return agentsByToken;
	}
	
	@Override
	public List<EAgent> getAgentsByTokenId(Long id) {
		List<EAgent> agentsByToken = this.findListByQuery("FROM EAgent a WHERE a.token.id = ?1", id);
		return agentsByToken;
	}
	
	@Override
	public List<EAgent> getAgentsWithoutToken() {
		List<EAgent> agents = this.findListByQuery("FROM EAgent a WHERE a.token IS EMPTY");
		return agents;
	}
	
	@Override
	public EAgent findAgentByName(String agentName) {
		EAgent agent = this.findByQuery("FROM EAgent a WHERE a.name = ?1", agentName);
		return agent;
	}
}
