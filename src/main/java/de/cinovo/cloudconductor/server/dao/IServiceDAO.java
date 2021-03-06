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

import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IServiceDAO extends IEntityDAO<EService, Long>, IFindNamed<EService> {
	
	/**
	 * @param names list of names
	 * @return list of services
	 */
	List<EService> findByName(Set<String> names);
	
	/**
	 * @param serviceName name of the service to be deleted
	 * @return number of service entities deleted
	 */
	int deleteByName(String serviceName);
	
	/**
	 * @return the row count
	 */
	Long count();
	
	/**
	 * @param pkg the package name
	 * @return list of services used by package
	 */
	List<EService> findByPackage(EPackage pkg);
	
	/**
	 * @param pkgId the package id
	 * @return list of services used by package
	 */
	List<EService> findByPackage(Long pkgId);
	
	/**
	 * @param serviceId the service id
	 * @return the name of the service
	 */
	String findNameById(Long serviceId);
}
