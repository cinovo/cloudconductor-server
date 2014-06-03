package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IFileTagsDAO;
import de.cinovo.cloudconductor.server.model.EFileTag;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("FileTagsDAOHib")
public class FileTagsDAOHib extends EntityDAOHibernate<EFileTag, Long> implements IFileTagsDAO {
	
	@Override
	public Class<EFileTag> getEntityClass() {
		return EFileTag.class;
	}
	
	@Override
	public EFileTag findByName(String name) {
		return this.findByQuery("FROM EFileTag t WHERE t.name = ?1", name);
	}
	
	@Override
	public List<EFileTag> findByIds(Long... id) {
		return this.findListByQuery("FROM EFileTag t WHERE t.id IN (?1)", Arrays.asList(id));
	}
}
