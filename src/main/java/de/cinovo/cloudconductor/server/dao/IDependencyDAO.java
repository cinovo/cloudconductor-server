package de.cinovo.cloudconductor.server.dao;

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

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IDependencyDAO extends IEntityDAO<EDependency, Long> {

	/**
	 * @param dep a dependency
	 * @return the database dependency object
	 */
	EDependency find(Dependency dep);
	
	/**
	 * @param dependencies the dependcie ids
	 * @return the objects
	 */
	List<EDependency> findByIds(Set<Long> dependencies);
}
