package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IDirectoryDAO;
import de.cinovo.cloudconductor.server.model.EDirectory;
import de.cinovo.cloudconductor.server.model.enums.AuditCategory;
import org.springframework.stereotype.Repository;

/**
 * Created by janweisssieker on 18.11.16.
 */
@Repository("DirectoryDAOHib")
public class DirectoryDAOHib extends AVersionedEntityHib<EDirectory> implements IDirectoryDAO {
    @Override
    protected AuditCategory getAuditCategory() {
        return AuditCategory.FILE;
    }

    @Override
    public Class<EDirectory> getEntityClass() {
        return EDirectory.class;
    }

    @Override
    public Long count() {
        return (Long) this.entityManager.createQuery(this.getVersionizedQuerry("SELECT COUNT(*) FROM EDirectory", "f")).getSingleResult();
    }

    @Override
    public EDirectory findByName(String name) {
        return this.findVersionedByQuery("FROM EDirectory c WHERE c.name = ?1", "c", name);
    }
}
