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

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("RepoDAOHib")
public class RepoDAOHib extends EntityDAOHibernate<ERepo, Long> implements IRepoDAO {

	@Override
	public Class<ERepo> getEntityClass() {
		return ERepo.class;
	}

	@Override
	public ERepo findByName(String name) {
		// language=HQL
		return this.findByQuery("FROM ERepo AS r WHERE r.name = ?1", name);
	}

	@Override
	public boolean exists(String repoName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(r) FROM ERepo AS r WHERE r.name = ?1", Long.class).setParameter(1, repoName).getSingleResult() > 0;
	}

	@Override
	public List<ERepo> findByNames(Iterable<String> repoNames) {
		//language=HQL
		return this.findListByQuery("FROM ERepo AS r WHERE r.name IN ?1", repoNames);
	}

	@Override
	public List<ERepo> findByTemplate(Long templateId) {
		// language=HQL
		return this.findListByQuery("SELECT r FROM ETemplate AS t JOIN t.repos AS r WHERE t.id = ?1", templateId);
	}

	@Override
	public List<String> findNamesByTemplate(String templateName) {
		// language=HQL
		String q = "SELECT r.name FROM ETemplate AS t JOIN t.repos AS r WHERE t.name = ?1";
		return this.entityManager.createQuery(q, String.class).setParameter(1, templateName).getResultList();
	}

	@Override
	public List<ERepo> findByTemplate(String templateName) {
		// language=HQL
		return this.findListByQuery("SELECT r FROM ETemplate AS t JOIN t.repos AS r WHERE t.name = ?1", templateName);
	}

}
