package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IUserGroupDAO extends IEntityDAO<EUserGroup, Long>, IFindNamed<EUserGroup> {
	/**
	 * @param userGroup the user group ids
	 * @return the user groups
	 */
	List<EUserGroup> findByIds(Iterable<Long> userGroup);
	
}
