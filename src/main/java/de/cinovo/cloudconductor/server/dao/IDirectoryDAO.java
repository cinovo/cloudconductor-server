package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EDirectory;
import de.taimos.dvalin.jpa.IEntityDAO;

/**
 * Created by janweisssieker on 18.11.16.
 */
public interface IDirectoryDAO extends IEntityDAO<EDirectory, Long>, IFindNamed<EDirectory> {
    public Long count();
}
