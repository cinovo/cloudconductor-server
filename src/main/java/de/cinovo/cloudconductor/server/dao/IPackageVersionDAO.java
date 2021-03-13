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

import java.util.Collection;
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
	 * @return list of package versions in given package
	 */
	List<EPackageVersion> findByPackage(Long pkgId);

	/**
	 * @param pkgName			the name of the package
	 * @param providingRepoIds  providing repo ids
	 * @return list of package version in given package
	 */
	List<EPackageVersion> findProvidedByPackage(String pkgName, Collection<Long> providingRepoIds);

	/**
	 * @param pkgName			name of the package to find versions for
	 * @param providingRepoIds	providing repo ids
	 * @param fixedMajor		fixed major version number
	 * @return list of provided package versions matching fixed major version
	 */
	List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor);

	/**
	 * @param pkgName			name of the package to find versions for
	 * @param providingRepoIds	providing repo ids
	 * @param fixedMajor		fixed major version number
	 * @param fixedMinor		fixed minor version number
	 * @return list of provided package versions matching fixed minor version
	 */
	List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor, String fixedMinor);

	/**
	/**
	 * @param pkgName			name of the package to find versions for
	 * @param providingRepoIds	providing repo ids
	 * @param fixedMajor		fixed major version number
	 * @param fixedMinor		fixed minor version number
	 * @param fixedPatch		fixed patch version number
	 * @return list of provided package versions matching fixed patch version
	 */
	List<EPackageVersion> findProvidedInRange(String pkgName, Collection<Long> providingRepoIds, String fixedMajor, String fixedMinor, String fixedPatch);

	/**
	 * @param baseName the package base name
	 * @param version  the version
	 * @return single package version
	 */
	EPackageVersion find(String baseName, String version);

	/**
	 * @param pkgName	name of the package
	 * @param version	version of the package
	 * @param repoIds	ids of the providing repos
	 * @return package version or null if not found or provided
	 */
	EPackageVersion findProvided(String pkgName, String version, Collection<Long> repoIds);
	
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
	 * @param repoIds the repo ids
	 * @return list of provided package versions
	 */
	List<EPackageVersion> findByRepo(Iterable<Long> repoIds);

	/**
	 * @param ids the package version ids
	 * @return the package versions
	 */
	List<EPackageVersion> findByIds(Iterable<Long> ids);
}
