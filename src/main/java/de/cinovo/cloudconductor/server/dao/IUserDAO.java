package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IUserDAO extends IEntityDAO<EUser, Long> {


	/**
	 * @param loginName the login name
	 * @return the user
	 */
	EUser findByLoginName(String loginName);
}
