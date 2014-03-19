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

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("ServiceDAOHib")
public class ServiceDAOHib extends EntityDAOHibernate<EService, Long> implements IServiceDAO {
	
	@Override
	public Class<EService> getEntityClass() {
		return EService.class;
	}
	
	@Override
	public EService findByName(String name) {
		return this.findByQuery("FROM EService s WHERE s.name = ?1", name);
	}
	
	@Override
	public List<EService> findByName(Set<String> names) {
		StringBuilder find = new StringBuilder();
		find.append("(");
		for (String n : names) {
			find.append("'" + n + "'");
		}
		find.append(")");
		return this.findListByQuery("FROM EService s WHERE s.name IN ?1", find);
	}
	
	@Override
	public Long count() {
		return (Long) this.entityManager.createQuery("SELECT COUNT(*) FROM EService").getSingleResult();
	}
	
}
