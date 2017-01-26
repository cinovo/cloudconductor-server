package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public interface IRepoDAO extends IEntityDAO<ERepo, Long>, IFindNamed<ERepo> {
	// nothing else to add
}
