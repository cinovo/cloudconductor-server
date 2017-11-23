package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

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
	public EAgent findAgentByName(String agentName) {
		return this.findByQuery("FROM EAgent a WHERE a.name = ?1", agentName);
	}
}
