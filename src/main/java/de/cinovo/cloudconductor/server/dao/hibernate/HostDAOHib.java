package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

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

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("HostDAOHib")
public class HostDAOHib extends EntityDAOHibernate<EHost, Long> implements IHostDAO {
	
	@Override
	public Class<EHost> getEntityClass() {
		return EHost.class;
	}
	
	@Override
	public EHost findByName(String name) {
		return this.findByQuery("FROM EHost h WHERE h.name = ?1", name);
	}
	
	@Override
	public Long count() {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		query.select(builder.count(query.from(EHost.class)));
		return this.entityManager.createQuery(query).getSingleResult();
	}

	@Override
	public EHost findByUuid(String uuid) {
		return this.findByQuery("FROM EHost h where h.uuid = ?1", uuid);
	}

}
