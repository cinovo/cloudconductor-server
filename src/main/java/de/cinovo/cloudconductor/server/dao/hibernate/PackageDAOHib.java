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

import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("PackageDAOHib")
public class PackageDAOHib extends EntityDAOHibernate<EPackage, Long> implements IPackageDAO {
	
	@Override
	protected String getFindListQuery() {
		return super.getFindListQuery() + " ORDER BY name";
	}
	
	@Override
	public Class<EPackage> getEntityClass() {
		return EPackage.class;
	}
	
	@Override
	public EPackage findByName(String name) {
		// language=HQL
		return this.findByQuery("FROM EPackage AS p WHERE p.name = ?1", name);
	}
	
	@Override
	public boolean exists(String packageName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(p) FROM EPackage AS p WHERE p.name = ?1", Long.class).setParameter(1, packageName).getSingleResult() > 0;
	}
	
	@Override
	public List<EPackage> findByName(Iterable<String> pkgNames) {
		// language=HQL
		return this.findListByQuery("FROM EPackage AS p WHERE p.name IN ?1", pkgNames);
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(p) FROM EPackage AS p", Long.class).getSingleResult();
	}
	
	@Override
	public String findName(Long id) {
		if (id == null) {
			return "";
		}
		// language=HQL
		String q = "SELECT p.name FROM EPackage AS p WHERE p.id = ?1";
		return this.entityManager.createQuery(q, String.class).setParameter(1, id).getSingleResult();
	}
	
	
	@Override
	public List<EPackage> findByIds(List<Long> packageIds) {
		if (packageIds == null || packageIds.isEmpty()) {
			return new ArrayList<>();
		}
		//language=HQL
		String q = "FROM EPackage AS p WHERE p.id IN ?1";
		return this.entityManager.createQuery(q, EPackage.class).setParameter(1, packageIds).getResultList();
	}
}
