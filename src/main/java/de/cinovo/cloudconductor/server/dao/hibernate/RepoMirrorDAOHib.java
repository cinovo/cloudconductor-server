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

import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("RepoMirrorDAOHib")
public class RepoMirrorDAOHib extends EntityDAOHibernate<ERepoMirror, Long> implements IRepoMirrorDAO {

	@Override
	public List<ERepoMirror> findByIds(Set<Long> ids) {
		// language=HQL
		return findListByQuery("FROM ERepoMirror AS m WHERE m.id IN ?1", ids);
	}

	@Override
	public List<ERepoMirror> findForRepo(ERepo repo) {
		// language=HQL
		return this.findListByQuery("FROM ERepoMirror AS m WHERE m.repo = ?1", repo);
	}

	@Override
	public List<ERepoMirror> findForRepo(String repoName) {
		// language=HQL
		return this.findListByQuery("FROM ERepoMirror AS m WHERE m.repo.name = ?1", repoName);
	}

	@Override
	public ERepoMirror findPrimaryForRepo(String repoName) {
		// language=HQL
		String q = "FROM ERepoMirror AS m JOIN FETCH m.repo AS r WHERE r.name = ?1 AND r.primaryMirrorId = m.id";
		return this.findByQuery(q, repoName);
	}

	@Override
	public int deleteForRepo(ERepo repo) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM ERepoMirror AS m WHERE m.repo = ?1").setParameter(1, repo).executeUpdate();
	}

	@Override
	public int deleteForRepo(String repoName) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM ERepoMirror AS m WHERE m.repo.name = ?1").setParameter(1, repoName).executeUpdate();
	}

	@Override
	public Class<ERepoMirror> getEntityClass() {
		return ERepoMirror.class;
	}
	
}
