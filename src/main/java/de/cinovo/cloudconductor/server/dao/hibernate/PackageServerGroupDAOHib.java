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

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("PackageServerGroupDAOHib")
public class PackageServerGroupDAOHib extends EntityDAOHibernate<EPackageServerGroup, Long> implements IPackageServerGroupDAO {
	
	@Override
	public Class<EPackageServerGroup> getEntityClass() {
		return EPackageServerGroup.class;
	}
	
	@Override
	public EPackageServerGroup findByName(String name) {
		return this.findByQuery("FROM EPackageServerGroup psg WHERE psg.name = ?1", name);
	}
	
}
