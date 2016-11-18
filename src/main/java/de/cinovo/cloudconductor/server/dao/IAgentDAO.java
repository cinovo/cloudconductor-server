package de.cinovo.cloudconductor.server.dao;

import java.util.List;

import de.cinovo.cloudconductor.server.model.EAgent;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public interface IAgentDAO extends IEntityDAO<EAgent, Long> {
	
	// nothing to add
	/**
	 * @param authToken the token of the AuthToken
	 * @return list of Agents in reference to that token
	 */
	public List<EAgent> getAgentsByToken(String authToken);
	
	/**
	 * @param id the id of the AuthToken
	 * @return List of agents in reference to that token
	 */
	public List<EAgent> getAgentsByTokenId(Long id);
	
	/**
	 * @return list of Agents that don't have a token yet
	 */
	public List<EAgent> getAgentsWithoutToken();
}
