package de.cinovo.cloudconductor.server.dao.hibernate;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Repository("FileDAOHib")
public class FileDAOHib extends EntityDAOHibernate<EFile, Long> implements IFileDAO {
	
	@Override
	public Class<EFile> getEntityClass() {
		return EFile.class;
	}
	
	@Override
	public EFile findByName(String name) {
		return this.findByQuery("FROM EFile c WHERE c.name = ?1", name);
	}
	
	@Override
	public Long count() {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		query.select(builder.count(query.from(EFile.class)));
		return this.entityManager.createQuery(query).getSingleResult();
	}
	
	@Override
	public List<EFile> findByTag(String... tagnames) {
		String query = "FROM EFile f WHERE f.tags.name IN ?1";
		return this.findListByQuery(query,  Arrays.asList(tagnames));
	}
	
	@Override
	public List<EFile> findByTemplate(String templateName) {
		return this.findListByQuery("FROM EFile f WHERE ?1 MEMBER OF f.templates", templateName);
	}
}
