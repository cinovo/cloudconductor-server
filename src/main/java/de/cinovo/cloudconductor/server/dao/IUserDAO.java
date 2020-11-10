package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

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

	/**
	 * @param loginName	the user login name
	 * @return true if user exists, false otherwise
	 */
	boolean existsByLogin(String loginName);

	/**
	 * @param loginName	the login name
	 * @return delete result
	 */
	int deleteByLoginName(String loginName);

	/**
	 * @param group the user group
	 * @return list of group members
	 */
	List<EUser> findByGroup(EUserGroup group);

}
