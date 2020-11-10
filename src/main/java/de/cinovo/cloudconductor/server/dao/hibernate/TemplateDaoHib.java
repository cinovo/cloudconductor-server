package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.api.model.SimpleTemplate;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("TemplateDaoHib")
public class TemplateDaoHib extends EntityDAOHibernate<ETemplate, Long> implements ITemplateDAO {
	
	@Override
	public Class<ETemplate> getEntityClass() {
		return ETemplate.class;
	}

	@Override
	public List<SimpleTemplate> findSimpleList() {
		// lamguage=HQL
		String q = "SELECT new de.cinovo.cloudconductor.api.model.SimpleTemplate(t.name, SIZE(h), SIZE(t.packageVersions), t.group)" +
				" FROM ETemplate AS t LEFT JOIN t.hosts AS h " +
				" GROUP BY t.name, t.group";
		return this.entityManager.createQuery(q, SimpleTemplate.class).getResultList();
	}

	@Override
	public ETemplate findByName(String templateName) {
		// language=HQL
		return this.findByQuery("FROM ETemplate AS t WHERE t.name = ?1", templateName);
	}
	
	@Override
	public List<ETemplate> findByName(Iterable<String> templateNames) {
		// language=HQL
		return this.findListByQuery("FROM ETemplate AS t WHERE t.name IN ?1", templateNames);
	}
	
	@Override
	public boolean exists(String templateName) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(t) FROM ETemplate AS t WHERE t.name = ?1", Long.class).setParameter(1, templateName).getSingleResult() > 0;
	}
	
	@Override
	public int disableAutoUpdate() {
		// language=HQL
		String q = "UPDATE ETemplate AS t SET t.autoUpdate = FALSE WHERE t.autoUpdate = TRUE";
		return this.entityManager.createQuery(q).executeUpdate();
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(t) FROM ETemplate AS t", Long.class).getSingleResult();
	}
	
	@Override
	public Long countUsingRepo(ERepo repo) {
		// language=HQL
		String q = "SELECT COUNT(t) FROM ETemplate As t WHERE ?1 MEMBER OF t.repos";
		return this.entityManager.createQuery(q, Long.class).setParameter(1, repo.getId()).getSingleResult();
	}
	
	@Override
	public Long countUsingPackageVersion(ERepo repo, EPackageVersion packageVersion) {
		// language=HQL
		String q = "SELECT COUNT(t) FROM ETemplate AS t WHERE ?1 MEMBER OF t.repos AND ?2 MEMBER OF t.packageVersions";
		return this.entityManager.createQuery(q, Long.class).setParameter(1, repo.getId()).setParameter(2, packageVersion.getId()).getSingleResult();
	}
	
	@Override
	public List<ETemplate> findByIds(Iterable<Long> templates) {
		if (templates == null || !templates.iterator().hasNext()) {
			return new ArrayList<>();
		}
		//language=HQL
		String q = "FROM ETemplate AS t WHERE t.id IN ?1";
		return this.findListByQuery(q, templates);
	}
	
	@Override
	public ETemplate withPackageVersions(List<Long> pvs) {
		return null;
	}
}
