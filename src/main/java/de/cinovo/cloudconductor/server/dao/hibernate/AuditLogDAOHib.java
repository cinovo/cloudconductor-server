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

import java.util.Collection;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IAuditLogDAO;
import de.cinovo.cloudconductor.server.model.EAuditLog;
import de.cinovo.cloudconductor.server.model.enums.AuditCategory;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("AuditLogDAOHib")
public class AuditLogDAOHib extends EntityDAOHibernate<EAuditLog, Long> implements IAuditLogDAO {
	
	@Override
	public Class<EAuditLog> getEntityClass() {
		return EAuditLog.class;
	}
	
	@Override
	public Collection<EAuditLog> byUser(String user) {
		return this.findListByQuery("FROM EAuditLog a WHERE a.username = ?1", user);
	}
	
	@Override
	public Collection<EAuditLog> byCategory(AuditCategory category) {
		return this.findListByQuery("FROM EAuditLog a WHERE a.category = ?1", category);
	}
	
}
