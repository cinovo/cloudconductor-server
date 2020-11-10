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

import de.cinovo.cloudconductor.server.model.ETemplate;
import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("SSHKeyDAOHib")
public class SSHKeyDAOHib extends EntityDAOHibernate<ESSHKey, Long> implements ISSHKeyDAO {
	
	@Override
	public Class<ESSHKey> getEntityClass() {
		return ESSHKey.class;
	}
	
	@Override
	public ESSHKey findByOwner(String owner) {
		// language=HQL
		return this.findByQuery("FROM ESSHKey AS k WHERE k.owner = ?1", owner);
	}
	
	@Override
	public List<ESSHKey> findByTemplate(ETemplate template) {
		// language=HQL
		return this.findListByQuery("FROM ESSHKey AS k WHERE ?1 in elements(k.templates) ", template.getId());
	}
	
	@Override
	public ESSHKey findByName(String name) {
		return this.findByOwner(name);
	}
	
	@Override
	public boolean exists(String name) {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(k) FROM ESSHKey AS k WHERE k.owner = ?1", Long.class).setParameter(1, name).getSingleResult() > 0;
	}
	
	@Override
	public Long count() {
		// language=HQL
		return this.entityManager.createQuery("SELECT COUNT(k) FROM ESSHKey AS k", Long.class).getSingleResult();
	}
}
