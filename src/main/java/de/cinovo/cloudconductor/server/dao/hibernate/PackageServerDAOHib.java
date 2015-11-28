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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("PackageServerDAOHib")
public class PackageServerDAOHib extends EntityDAOHibernate<EPackageServer, Long> implements IPackageServerDAO {
	
	@Override
	public Class<EPackageServer> getEntityClass() {
		return EPackageServer.class;
	}
	
	@Override
	public List<EPackageServer> findForGroup(EPackageServerGroup group) {
		return this.findListByQuery("FROM EPackageServer ps WHERE ps.serverGroup = ?1", group.getId());
	}
	
	@Override
	public List<EPackageServer> findOnePerGroup() {
		Session session = (Session) this.entityManager.getDelegate();
		Criteria criteria = session.createCriteria(EPackageServer.class);
		criteria.setProjection(Projections.distinct(Projections.property("id")));
		criteria.setResultTransformer(Transformers.aliasToBean(EPackageServer.class));
		return criteria.list();
	}
	
}
