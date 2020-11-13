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

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("FileDAOHib")
public class FileDAOHib extends EntityDAOHibernate<EFile, Long> implements IFileDAO {
	
	@Override
	public Class<EFile> getEntityClass() {
		return EFile.class;
	}
	
	@Override
	public EFile findByName(String name) {
		// language=HQL
		return this.findByQuery("FROM EFile AS f WHERE f.name = ?1", name);
	}
	
	@Override
	public boolean exists(String fileName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(f) FROM EFile AS f WHERE f.name = ?1", Long.class).setParameter(1, fileName).getSingleResult() > 0;
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(f) FROM EFile AS f", Long.class).getSingleResult();
	}
	
	@Override
	public List<EFile> findByTemplate(String templateName) {
		// language=HQL
		return this.findListByQuery("FROM EFile AS f WHERE ?1 in elements(f.templates)", templateName);
	}
	
	@Override
	public List<EFile> findByPackage(EPackage pkg) {
		// language=HQL
		return this.findListByQuery("FROM EFile AS f WHERE f.pkgId = ?1", pkg.getId());
	}
}
