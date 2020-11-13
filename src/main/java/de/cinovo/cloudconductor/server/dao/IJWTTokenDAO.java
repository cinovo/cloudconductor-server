package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IJWTTokenDAO extends IEntityDAO<EJWTToken, Long> {
	
	/**
	 * @param token the token
	 * @return the {@link EAuthToken}
	 */
	EJWTToken findByToken(String token);
	
	/**
	 * @param token the JWT string to be deleted
	 */
	void deleteByToken(String token);
	
	/**
	 * @param refToken the auth token reference for which JWTs should be deleted
	 */
	void deleteByRefToken(EAuthToken refToken);
	
	/**
	 * @param user the user
	 */
	void deleteByUser(EUser user);
}
