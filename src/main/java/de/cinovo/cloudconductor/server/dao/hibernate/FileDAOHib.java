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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileTag;
import de.cinovo.cloudconductor.server.model.enums.AuditCategory;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Repository("FileDAOHib")
public class FileDAOHib extends AVersionedEntityHib<EFile> implements IFileDAO {
	
	@Override
	public Class<EFile> getEntityClass() {
		return EFile.class;
	}
	
	@Override
	public EFile findByName(String name) {
		return this.findVersionedByQuery("FROM EFile c WHERE c.name = ?1", "c", name);
	}
	
	@Override
	public Long count() {
		return (Long) this.entityManager.createQuery(this.getVersionizedQuerry("SELECT COUNT(*) FROM EFile", "f")).getSingleResult();
	}
	
	@Override
	public List<EFile> findByTag(String... tagnames) {
		String query = "FROM EFile f WHERE f.tags.name IN ?1";
		return this.findVersionedListByQuery(query, "f", Arrays.asList(tagnames));
	}
	
	@Override
	public List<EFile> findByTag(EFileTag... tags) {
		List<EFile> findList = this.findList();
		List<EFile> result = new ArrayList<>();
		List<EFileTag> taged = Arrays.asList(tags);
		for (EFile f : findList) {
			if (!Collections.disjoint(f.getTags(), taged)) {
				result.add(f);
			}
		}
		return result;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.FILE;
	}
}
