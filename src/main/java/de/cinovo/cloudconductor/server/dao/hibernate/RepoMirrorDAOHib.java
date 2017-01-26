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
	public Class<ERepoMirror> getEntityClass() {
		return ERepoMirror.class;
	}

	@Override
	public List<ERepoMirror> findForRepo(ERepo repo) {
		return this.findListByQuery("FROM ERepoMirror mirror WHERE mirror.repo = ?1", repo);
	}
	
}
