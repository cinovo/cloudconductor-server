package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.IEntityDAO;

import javax.annotation.Nullable;
import java.util.List;

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
	 * @param user  the user
	 * @param token the token string
	 * @return the auth token entity or null if not found
	 */
	@Nullable
	EAuthToken findByUserAndToken(EUser user, String token);
	
	/**
	 * @param id user id
	 * @return the auth tokens of the user
	 */
	List<EAuthToken> findByUser(Long id);
}
