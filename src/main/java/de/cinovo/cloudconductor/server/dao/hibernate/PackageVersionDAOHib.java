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

import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("PackageVersionDAOHib")
public class PackageVersionDAOHib extends EntityDAOHibernate<EPackageVersion, Long> implements IPackageVersionDAO {

	@Override
	public Class<EPackageVersion> getEntityClass() {
		return EPackageVersion.class;
	}
	
	@Override
	public List<EPackageVersion> findByPackage(EPackage pkg) {
		return this.findByPackage(pkg.getId());
	}
	
	@Override
	public List<EPackageVersion> findByPackage(Long pkgId) {
		return this.findListByQuery("FROM EPackageVersion pv WHERE pv.pkgId = ?1", pkgId);
	}

	@Override
	public EPackageVersion findProvided(String pkgName, String version, Collection<Long> repoIds) {
		if (repoIds.isEmpty()) {
			return null;
		}
		// language=HQL
		String query = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos" +
				" WHERE pv.pkgName = ?1 AND pv.version= ?2 AND repos IN ?3";
		return this.findListByQuery(query, pkgName, version, repoIds).stream().findFirst().orElse(null);
	}

	@Override
	public List<EPackageVersion> findProvidedByPackage(String pkgName, Collection<Long> providingRepoIds) {
		if (providingRepoIds.isEmpty()) {
			return Collections.emptyList();
		}
		// language=HQL
		String query = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos" +
				" WHERE pv.pkgName = ?1 AND repos IN ?2";
		return this.findListByQuery(query, pkgName, providingRepoIds);
	}

	@Override
	public List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor) {
		if (providingRepoIds.isEmpty()) {
			return Collections.emptyList();
		}
		// language=HQL
		String query = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos" +
				" WHERE pv.pkgName = ?1 AND repos IN ?2" +
				" AND (pv.version LIKE concat(?3, '.', '%') OR pv.version LIKE concat(?3, '-', '%'))";
		return this.findListByQuery(query, pkgName, providingRepoIds, fixedMajor);
	}

	@Override
	public List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor, String fixedMinor) {
		if (providingRepoIds.isEmpty()) {
			return Collections.emptyList();
		}
		// language=HQL
		String q = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos" +
				" WHERE pv.pkgName = ?1 AND repos IN ?2" +
				" AND (pv.version LIKE concat(?3, '.', ?4, '.', '%') OR pv.version LIKE concat(?3, '.', ?4, '-', '%'))";
		return this.findListByQuery(q, pkgName, providingRepoIds, fixedMajor, fixedMinor);
	}

	@Override
	public List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor, String fixedMinor, String fixedPatch) {
		if (providingRepoIds.isEmpty()) {
			return Collections.emptyList();
		}
		// language=HQL
		String q = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos" +
				" WHERE pv.pkgName = ?1 AND repos IN ?2" +
				" AND (pv.version LIKE concat(?3, '.', ?4, '.', ?5, '.', '%') OR pv.version LIKE concat(?3, '.', ?4, '.', ?5, '-', '%'))";
		return this.findListByQuery(q, pkgName, providingRepoIds, fixedMajor, fixedMinor, fixedPatch);
	}
	
	@Override
	public EPackageVersion find(String baseName, String version) {
		// language=HQL
		return this.findByQuery("FROM EPackageVersion pv WHERE pv.pkgName = ?1 AND pv.version = ?2", baseName, version);
	}

	@Override
	public List<EPackageVersion> find(String baseName) {
		// language=HQL
		return this.findListByQuery("FROM EPackageVersion pv WHERE pv.pkgName = ?1", baseName);
	}
	
	@Override
	public List<EPackageVersion> findByRepo(Long id) {
		// language=HQL
		return this.findListByQuery("FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos WHERE ?1 IN repos", id);
	}

	@Override
	public List<EPackageVersion> findByRepo(Iterable<Long> repoIds) {
		if (repoIds == null || !repoIds.iterator().hasNext()) {
			return Collections.emptyList();
		}
		// language=HQL
		String q = "FROM EPackageVersion AS pv LEFT JOIN pv.repos AS repos WHERE repos IN ?1 ORDER BY pv.pkgName";
		return this.findListByQuery(q, repoIds);
	}

	@Override
	public List<EPackageVersion> findByIds(Iterable<Long> ids) {
		if (ids == null || !ids.iterator().hasNext()) {
			return Collections.emptyList();
		}
		//language=HQL
		String q = "FROM EPackageVersion AS pv WHERE pv.id IN ?1 ORDER BY pv.pkgName";
		return this.findListByQuery(q, ids);
	}
	
}
