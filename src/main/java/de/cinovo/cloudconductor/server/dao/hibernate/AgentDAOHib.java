package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 *
 * @author ablehm
 */
@Repository("AgentDAOHib")
public class AgentDAOHib extends EntityDAOHibernate<EAgent, Long> implements IAgentDAO {
	
	@Override
	public Class<EAgent> getEntityClass() {
		return EAgent.class;
	}
	
	@Override
	public EAgent findAgentByName(String agentName) {
		// language=HQL
		return this.findByQuery("FROM EAgent AS a WHERE a.name = ?1", agentName);
	}
	
	@Override
	public String findName(Long agentId) {
		// language=HQL
		String q = "SELECT a.name FROM EAgent AS a WHERE a.id = ?1";
		return this.entityManager.createQuery(q, String.class).setParameter(1, agentId).getSingleResult();
	}
	
	@Override
	public List<EAgent> findByUser(Long id) {
		// language=HQL
		return this.findListByQuery("FROM EAgent AS a WHERE a.userid = ?1", id);
	}
}
