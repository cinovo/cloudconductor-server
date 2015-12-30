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

import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.taimos.dvalin.jpa.EntityDAOHibernate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("ServerOptionsDAOHib")
public class ServerOptionsDAOHib extends EntityDAOHibernate<EServerOptions, Long> implements IServerOptionsDAO {
	
	@Override
	public Class<EServerOptions> getEntityClass() {
		return EServerOptions.class;
	}
	
	@Override
	public EServerOptions get() {
		List<EServerOptions> findList = this.findList();
		if (findList.isEmpty()) {
			return new EServerOptions();
		}
		return findList.iterator().next();
	}
	
}
