package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

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
	
	@Override
	public List<EHost> findHostsForTemplate(String templateName) {
		return this.findListByQuery("FROM EHost h WHERE h.template.name = ?1", templateName);
	}
	
	@Override
	public List<SimpleHost> findSimpleHosts() {
		StringBuilder query = this.getSimpleHostQuery();
		TypedQuery<SimpleHost> tq = this.entityManager.createQuery(query.toString(), SimpleHost.class);
		return tq.getResultList();
	}
	
	@Override
	public SimpleHost findSimpleHost(Long id) {
		StringBuilder query = this.getSimpleHostQuery();
		query.append(" WHERE h.id = ?1");
		TypedQuery<SimpleHost> tq = this.entityManager.createQuery(query.toString(), SimpleHost.class);
		tq.setParameter(1, id);
		List<SimpleHost> resultList = tq.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}
	
	private StringBuilder getSimpleHostQuery() {
		StringBuilder query = new StringBuilder();
		query.append("SELECT new de.cinovo.cloudconductor.api.model.SimpleHost(h.name, agent.name, h.uuid, h.template.name, h.lastSeen, ");
		query.append("(SELECT count(ss) FROM EServiceState ss WHERE ss.host.id = h.id), ");
		query.append("(SELECT count(ps) FROM EPackageState ps WHERE ps.host.id = h.id) ");
		query.append(") FROM EHost h");
		return query;
	}
	
}
