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
import de.cinovo.cloudconductor.server.model.EPackageVersion;
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
@Repository("PackageVersionDAOHib")
public class PackageVersionDAOHib extends EntityDAOHibernate<EPackageVersion, Long> implements IPackageVersionDAO {
	
	@Override
	public Class<EPackageVersion> getEntityClass() {
		return EPackageVersion.class;
	}
	
	@Override
	public EPackageVersion find(String baseName, String version) {
		// language=HQL
		return this.findByQuery("FROM EPackageVersion pv WHERE pv.pkg.name = ?1 AND pv.version = ?2", baseName, version);
	}
	
	@Override
	public List<EPackageVersion> find(String baseName) {
		// language=HQL
		return this.findListByQuery("FROM EPackageVersion pv WHERE pv.pkg.name = ?1", baseName);
	}

	@Override
	public List<EPackageVersion> findByTemplate(Long templateId) {
		// language=HQL
		return this.findListByQuery("SELECT pv FROM ETemplate t JOIN t.packageVersions AS pv WHERE t.id = ?1", templateId);
	}

	@Override
	public List<EPackageVersion> findByTemplate(String templateName) {
		// language=HQL
		return this.findListByQuery("SELECT pv FROM ETemplate t JOIN t.packageVersions AS pv WHERE t.name = ?1", templateName);
	}

	@Override
	public List<EPackageVersion> findByRepo(String repoName) {
		// language=HQL
		return findListByQuery("FROM EPackageVersion AS pv JOIN FETCH pv.repos AS r WHERE r.name = ?1", repoName);
	}

}
