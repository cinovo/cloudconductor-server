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

import de.cinovo.cloudconductor.api.model.ServiceDefaultState;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
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
@Repository("ServiceDefaultStateDAOHib")
public class ServiceDefaultStateDAOHib extends EntityDAOHibernate<EServiceDefaultState, Long> implements IServiceDefaultStateDAO {
	
	@Override
	public Class<EServiceDefaultState> getEntityClass() {
		return EServiceDefaultState.class;
	}
	
	@Override
	public EServiceDefaultState findByName(String serviceName, String templateName) {
		// language=HQL
		return this.findByQuery("FROM EServiceDefaultState as ss WHERE ss.service.name = ?1 AND ss.template.name = ?2", serviceName, templateName);
	}

	@Override
	public ServiceDefaultState findFlatByName(String templateName, String serviceName) {
		// language=HQL
		String query = "SELECT NEW de.cinovo.cloudconductor.api.model.ServiceDefaultState(s.template.name, s.service.name, s.state)" +
				" FROM EServiceDefaultState AS s WHERE s.template.name = ?1 AND s.service.name = ?2";
		return this.entityManager.createQuery(query, ServiceDefaultState.class) //
				.setParameter(1, templateName).setParameter(2, serviceName) //
				.getSingleResult();
	}

	@Override
	public List<EServiceDefaultState> findByTemplate(String templateName) {
		// language=HQL
		return this.findListByQuery("FROM EServiceDefaultState AS ss WHERE ss.template.name = ?1", templateName);
	}

	@Override
	public List<ServiceDefaultState> findFlatByTemplate(String templateName) {
		// language=HQL
		String query = "SELECT NEW de.cinovo.cloudconductor.api.model.ServiceDefaultState(s.template.name, s.service.name, s.state)" +
				" FROM EServiceDefaultState AS s WHERE s.template.name = ?1";
		return this.entityManager.createQuery(query, ServiceDefaultState.class).setParameter(1, templateName).getResultList();
	}
}
