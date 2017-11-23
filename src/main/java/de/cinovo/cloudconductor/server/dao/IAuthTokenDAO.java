package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IAuthTokenDAO extends IEntityDAO<EAuthToken, Long> {

	/**
	 * @param token the token to check
	 * @return true if token is unique
	 */
	boolean isTokenUnique(String token);

	/**
	 * @param token the token
	 * @return the {@link EAuthToken}
	 */
	EAuthToken findByToken(String token);
}
