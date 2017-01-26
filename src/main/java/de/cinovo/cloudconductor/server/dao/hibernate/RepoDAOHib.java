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

import de.cinovo.cloudconductor.server.model.ERepo;
import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("RepoDAOHib")
public class RepoDAOHib extends EntityDAOHibernate<ERepo, Long> implements IRepoDAO {
	
	@Override
	public Class<ERepo> getEntityClass() {
		return ERepo.class;
	}
	
	@Override
	public ERepo findByName(String name) {
		return this.findByQuery("FROM ERepo psg WHERE psg.name = ?1", name);
	}
	
}
