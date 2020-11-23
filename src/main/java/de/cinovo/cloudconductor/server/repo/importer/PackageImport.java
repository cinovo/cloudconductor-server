package de.cinovo.cloudconductor.server.repo.importer;

/*
 * #%L cloudconductor-server %% Copyright (C) 2013 - 2014 Cinovo AG %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.handler.PackageHandler;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageImport implements IPackageImport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PackageImport.class);
	
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private PackageHandler packageHandler;
	
	
	/**
	 * @param repo            the repo
	 * @param packageVersions the package versions
	 */
	@Override
	@Transactional
	public void importVersions(ERepo repo, Set<PackageVersion> packageVersions) {
		if (packageVersions.isEmpty()) {
			return;
		}
		if (repo == null) {
			return;
		}
		this.updateRepoPackageVersions(repo, packageVersions);
	}
	
	private void updateRepoPackageVersions(ERepo repo, Set<PackageVersion> packageVersions) {
		Set<Long> newRepoProvision = new HashSet<>();
		List<EPackageVersion> oldRepoProvision = this.packageVersionDAO.findByRepo(repo.getId());
		
		for (PackageVersion providedVersion : packageVersions) {
			// Retrieve the package for the given providedVersion. Create it if it doesn't exist.
			EPackage pkg = this.packageDAO.findByName(providedVersion.getName());
			if (pkg == null) { // there is no package for this providedVersion yet
				pkg = this.packageHandler.createPackageFromVersion(providedVersion);
			}
			EPackageVersion pkgVersion = this.packageVersionDAO.find(pkg.getName(), providedVersion.getVersion());
			if (pkgVersion == null) {
				PackageImport.LOGGER.debug("Create new package version '" + pkg.getName() + "':'" + providedVersion.getVersion() + "'");
				pkgVersion = this.packageHandler.createEntity(providedVersion, pkg);
			} else if (!pkgVersion.getRepos().contains(repo.getId())) {
				PackageImport.LOGGER.debug("Update existing package version '" + pkg.getName() + "':'" + providedVersion.getVersion() + "'");
				pkgVersion = this.packageHandler.updateEntity(pkgVersion, repo);
			}
			newRepoProvision.add(pkgVersion.getId());
		}
		
		
		for (EPackageVersion oldProvision : oldRepoProvision) {
			if (!newRepoProvision.contains(oldProvision.getId())) {
				oldProvision.getRepos().remove(repo.getId());
				this.packageVersionDAO.save(oldProvision);
			}
		}
		
	}
	
}
