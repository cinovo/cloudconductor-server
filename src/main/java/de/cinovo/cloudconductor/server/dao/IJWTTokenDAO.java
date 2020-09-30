package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

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
	 * @param token	the JWT string to be deleted
	 * @return number of tokens deleted
	 */
	int deleteByToken(String token);

	/**
	 * @param refToken the reference token
	 * @return the jwt token
	 */
	List<EJWTToken> findByRefToken(EAuthToken refToken);

	/**
	 * @param refToken	the auth token reference for which JWTs should be deleted
	 * @return number of JWTs deleted
	 */
	int deleteByRefToken(EAuthToken refToken);

	int deleteByUser(EUser user);
}
