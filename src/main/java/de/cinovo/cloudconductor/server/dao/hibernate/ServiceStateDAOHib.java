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

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("ServiceStateDAOHib")
public class ServiceStateDAOHib extends EntityDAOHibernate<EServiceState, Long> implements IServiceStateDAO {
	
	@Override
	public Class<EServiceState> getEntityClass() {
		return EServiceState.class;
	}
	
	@Override
	public EServiceState findByServiceAndHost(Long serviceId, Long host) {
		// language=HQL
		String q = "FROM EServiceState as ss WHERE ss.serviceId = ?1 AND ss.hostId = ?2";
		return this.findByQuery(q, serviceId, host);
	}
	
	@Override
	public List<EServiceState> findByHost(Long id) {
		// language=HQL
		String q = "FROM EServiceState as ss WHERE ss.hostId = ?1";
		return this.findListByQuery(q, id);
	}
}
