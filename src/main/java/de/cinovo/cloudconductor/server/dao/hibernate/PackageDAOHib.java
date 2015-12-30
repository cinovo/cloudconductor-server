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

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("PackageDAOHib")
public class PackageDAOHib extends EntityDAOHibernate<EPackage, Long> implements IPackageDAO {
	
	@Override
	public Class<EPackage> getEntityClass() {
		return EPackage.class;
	}
	
	@Override
	public EPackage findByName(String name) {
		return this.findByQuery("FROM EPackage p WHERE p.name = ?1", name);
	}
	
	@Override
	public Long count() {
		return (Long) this.entityManager.createQuery("SELECT COUNT(*) FROM EPackage").getSingleResult();
	}
	
	@Override
	public List<EPackage> findNotUsedPackage(EService service) {
		if (service.getPackages().isEmpty()) {
			return this.findList();
		}
		String query = "FROM EPackage as p WHERE p not in ?1";
		return this.findListByQuery(query, service.getPackages());
	}
}
