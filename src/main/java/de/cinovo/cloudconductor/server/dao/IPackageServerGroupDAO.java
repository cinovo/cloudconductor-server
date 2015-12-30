package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public interface IPackageServerGroupDAO extends IEntityDAO<EPackageServerGroup, Long>, IFindNamed<EPackageServerGroup> {
	// nothing else to add
}
