package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public interface IAgentAuthTokenDAO extends IEntityDAO<EAgentAuthToken, Long> {
	
	/**
	 * @param authToken the token to check for uniqueness
	 * @return true if token is unique in db, false otherwise
	 */
	public boolean isTokenUnique(String authToken);
}
