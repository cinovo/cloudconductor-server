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
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		return entityManager.createQuery("SELECT COUNT(p) FROM EPackage AS p WHERE p.name = ?1", Long.class).setParameter(1, packageName).getSingleResult() > 0;
	}

	@Override
	public List<EPackage> findEmpty() {
		// language=HQL
		return this.findListByQuery("FROM EPackage AS p WHERE p.versions IS EMPTY");
	}

	@Override
	public List<EPackage> findByName(Iterable<String> pkgNames) {
		// language=HQL
		return this.findListByQuery("FROM EPackage AS p WHERE p.name IN ?1", pkgNames);
	}

	@Override
	public List<EPackage> findByService(String serviceName) {
		// language=HQL
		return this.findListByQuery("SELECT p FROM EService AS s JOIN s.packages AS p WHERE s.name = ?1", serviceName);
	}

	@Override
	public List<String> findNamesByService(String serviceName) {
		// language=HQL
		String q = "SELECT p.name FROM EService AS s JOIN s.packages AS p WHERE s.name = ?1";
		return this.entityManager.createQuery(q, String.class).setParameter(1, serviceName).getResultList();
	}

	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(p) FROM EPackage AS p", Long.class).getSingleResult();
	}
	
	@Override
	public List<EPackage> findNotUsedPackage(EService service) {
		// TODO reimplement query
		if (service.getPackages().isEmpty()) {
			return this.findList();
		}
		String query = "FROM EPackage as p WHERE p not in ?1";
		return this.findListByQuery(query, service.getPackages());
	}

	public Map<String, String> findServiceUsage(String serviceName) {
		// language=HQL
		String q = "SELECT t.name, p.name" +
				" FROM ETemplate AS t JOIN t.packageVersions AS tv," +
				" EService AS s JOIN s.packages AS p JOIN p.versions AS pv" +
				" WHERE tv IN elements(pv) AND s.name = ?1";
		return this.entityManager.createQuery(q, Tuple.class).setParameter(1, serviceName).getResultList()
				.stream().collect(Collectors.toMap(t -> t.get(0, String.class), t -> t.get(1, String.class), (a,b) ->b));
	}

	@Override
	public Map<String, String> findPackageUsage(String pkgName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT t.name, pv.version FROM ETemplate AS t JOIN t.packageVersions AS pv WHERE pv.pkg.name = ?1", Tuple.class)
				.setParameter(1, pkgName).getResultList()
				.stream().collect(Collectors.toMap(t -> t.get(0, String.class), t -> t.get(1, String.class)));
	}
}
