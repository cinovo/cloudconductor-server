package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
		// language=HQL
		return this.findByQuery("FROM EHost AS h WHERE h.name = ?1", name);
	}
	
	@Override
	public boolean exists(String name) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(h) FROM EHost AS h WHERE h.name = ?1", Long.class).getSingleResult() > 0;
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(h) FROM EHost AS h ", Long.class).getSingleResult();
	}
	
	@Override
	public Long countForTemplate(Long templateId) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(h) FROM EHost AS h WHERE h.templateId = ?1", Long.class).setParameter(1, templateId).getSingleResult();
	}
	
	@Override
	public EHost findByUuid(String uuid) {
		// language=HQL
		return this.findByQuery("FROM EHost AS h WHERE h.uuid = ?1", uuid);
	}
	
	@Override
	public List<EHost> findHostsForTemplate(Long templateId) {
		// language=HQL
		return this.findListByQuery("SELECT h FROM EHost AS h WHERE h.templateId = ?1", templateId);
	}
	
}
