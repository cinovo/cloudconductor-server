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

import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("ServiceDAOHib")
public class ServiceDAOHib extends EntityDAOHibernate<EService, Long> implements IServiceDAO {

	@Override
	public Class<EService> getEntityClass() {
		return EService.class;
	}

	@Override
	public EService findByName(String name) {
		// language=HQL
		return this.findByQuery("FROM EService AS s WHERE s.name = ?1", name);
	}

	@Override
	public int deleteByName(String serviceName) {
		// language=HQL
		return this.entityManager.createQuery("DELETE FROM EService AS s WHERE s.name = ?1").setParameter(1, serviceName).executeUpdate();
	}

	@Override
	public boolean exists(String serviceName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(s) FROM EService AS s WHERE s.name = ?1", Long.class).setParameter(1, serviceName).getSingleResult() > 0;
	}

	@Override
	public List<EService> findByName(Set<String> names) {
		// language=HQL
		return this.findListByQuery("FROM EService AS s WHERE s.name IN ?1", names);
	}

	@Override
	public List<EService> findByPackage(EPackage pkg) {
		// language=HQL
		return this.findListByQuery("FROM EService AS s WHERE ?1 in elements(s.packages)", pkg.getId());
	}
	
	@Override
	public List<EService> findByPackage(Long pkgId) {
		// language=HQL
		return this.findListByQuery("FROM EService AS s WHERE ?1 in elements(s.packages)", pkgId);
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(s) FROM EService AS s", Long.class).getSingleResult();
	}

}
