package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.EDirectory;

/**
 * Created by janweisssieker on 18.11.16.
 */
public interface IDirectoryDAO extends IAuditedEntity<EDirectory, Long>, IFindNamed<EDirectory> {
    public Long count();
}
