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
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IPackageVersionDAO extends IEntityDAO<EPackageVersion, Long> {
	
	/**
	 * @param pkg the EPackage
	 * @return list of package versions
	 */
	List<EPackageVersion> findByPackage(EPackage pkg);
	
	/**
	 * @param pkgId the pkgId
	 * @return list of package versions
	 */
	List<EPackageVersion> findByPackage(Long pkgId);
	
	/**
	 * @param baseName the package base name
	 * @param version  the version
	 * @return single package version
	 */
	EPackageVersion find(String baseName, String version);
	
	/**
	 * @param baseName the base name
	 * @return list of package versions
	 */
	List<EPackageVersion> find(String baseName);
	
	/**
	 * @param id the id
	 * @return the versions
	 */
	List<EPackageVersion> findByRepo(Long id);
	
	/**
	 * @param ids the package version ids
	 * @return the package versions
	 */
	List<EPackageVersion> findByIds(Iterable<Long> ids);
}
