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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("RepoMirrorDAOHib")
public class RepoMirrorDAOHib extends EntityDAOHibernate<ERepoMirror, Long> implements IRepoMirrorDAO {
	
	@Override
	public List<ERepoMirror> findByIds(Set<Long> ids) {
		if (ids == null || !ids.isEmpty()) {
			return new ArrayList<>();
		}
		// language=HQL
		return this.findListByQuery("FROM ERepoMirror AS m WHERE m.id IN ?1", ids);
	}
	
	@Override
	public List<ERepoMirror> findForRepo(ERepo repo) {
		// language=HQL
		return this.findListByQuery("FROM ERepoMirror AS m WHERE m.repoId = ?1", repo.getId());
	}
	
	
	@Override
	public void deleteForRepo(ERepo repo) {
		if (repo == null) {
			return;
		}
		// language=HQL
		this.entityManager.createQuery("DELETE FROM ERepoMirror AS m WHERE m.repoId = ?1").setParameter(1, repo.getId()).executeUpdate();
	}
	
	@Override
	public Long countForRepo(Long id) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(m) FROM ERepoMirror AS m WHERE m.repoId = ?1", Long.class).setParameter(1, id).getSingleResult();
	}
	
	@Override
	public Class<ERepoMirror> getEntityClass() {
		return ERepoMirror.class;
	}
	
}
