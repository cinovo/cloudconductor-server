package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.taimos.dvalin.jpa.IEntityDAO;

import javax.annotation.Nullable;

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
	 * @return the {@link EAuthToken} or null if not found
	 */
	@Nullable
	EAuthToken findByToken(String token);

	/**
	 * @param userName	the user login name
	 * @param token		the token string
	 * @return the auth token entity or null if not found
	 */
	@Nullable
	EAuthToken findByUserAndToken(String userName, String token);
}
