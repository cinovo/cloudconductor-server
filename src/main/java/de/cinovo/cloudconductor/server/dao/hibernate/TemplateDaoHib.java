package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.ArrayList;

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

import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("TemplateDaoHib")
public class TemplateDaoHib extends EntityDAOHibernate<ETemplate, Long> implements ITemplateDAO {
	
	@Override
	public Class<ETemplate> getEntityClass() {
		return ETemplate.class;
	}
	
	@Override
	public ETemplate findByName(String name) {
		return this.findByQuery("FROM ETemplate t WHERE t.name = ?1", name);
	}
	
	@Override
	public List<ETemplate> findByPackageServer(EPackageServer packageServer) {
		return this.findListByQuery("FROM ETemplate t WHERE ? IN elements(t.packageServers)", packageServer);
	}
	
}
