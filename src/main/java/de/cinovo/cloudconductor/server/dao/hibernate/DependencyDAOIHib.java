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

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
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
 * 
 */
@Repository("DependencyDAOIHib")
public class DependencyDAOIHib extends EntityDAOHibernate<EDependency, Long> implements IDependencyDAO {
	
	@Override
	public Class<EDependency> getEntityClass() {
		return EDependency.class;
	}
	
	@Override
	public EDependency find(Dependency dep) {
		// language=HQL
		return this.findByQuery("FROM EDependency d WHERE d.name = ?1 AND d.type = ?2 AND d.operator = ?3 AND d.version = ?4", dep.getName(), dep.getType(), dep.getOperator(), dep.getVersion());
	}
	
	@Override
	public List<EDependency> findByIds(Set<Long> dependencies) {
		if (dependencies == null || dependencies.isEmpty()) {
			return new ArrayList<>();
		}
		// language=HQL
		return this.findListByQuery("FROM EDependency AS r WHERE r.id IN ?1", dependencies);
	}
	
}
