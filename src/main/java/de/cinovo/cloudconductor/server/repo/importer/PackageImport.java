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
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.PackageHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.restutils.RESTAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

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
	private IFileDAO fileDAO;
	@Autowired
	private IPackageVersionDAO versionDAO;
	@Autowired
	private IRepoDAO repoDAO;

	@Autowired
	private PackageHandler packageHandler;
	@Autowired
	private TemplateHandler templateHandler;


	/**
	 * @param packageVersions the package versions
	 */
	@Override
	public void importVersions(Set<PackageVersion> packageVersions) {
		Map<String, Set<PackageVersion>> repoMap = new HashMap<>();
		for(PackageVersion packageVersion : packageVersions) {
			for(String repo : packageVersion.getRepos()) {
				if(!repoMap.containsKey(repo)) {
					repoMap.put(repo, new HashSet<>());
				}
				repoMap.get(repo).add(packageVersion);
			}
		}

		for(Entry<String, Set<PackageVersion>> entry : repoMap.entrySet()) {
			this.importVersions(entry.getValue(), entry.getKey());
		}
	}

	public void importVersions(Set<PackageVersion> packageVersions, String repoName) {
		RESTAssert.assertNotEmpty(packageVersions);
		Map<String, Set<EPackageVersion>> provided = new HashMap<>();
		ERepo repo = this.repoDAO.findByName(repoName);
		for(PackageVersion version : packageVersions) {
			// Retrieve the package for the given version. Create it if it doesn't exist.
			EPackage epackage = this.packageDAO.findByName(version.getName());
			if(epackage == null) { // there is no package for this version yet
				epackage = this.packageHandler.createPackageFromVersion(version);
			}

			String name = version.getName();
			String ver = version.getVersion();

			// Check if we have this particular package version on record.
			EPackageVersion eversion = this.versionDAO.find(name, ver);
			if(eversion == null) {
				PackageImport.LOGGER.debug("Create new package version '" + name + "':'" + ver + "'");
				eversion = this.packageHandler.createEntity(version, epackage);
			} else {
				PackageImport.LOGGER.debug("Update existing package version '" + name + "':'" + ver + "'");
				boolean containsRepo = false;
				for(ERepo eRepo : eversion.getRepos()) {
					if(eRepo.getName().equalsIgnoreCase(repoName)) {
						containsRepo = true;
						break;
					}
				}
				if(!containsRepo) {
					eversion = this.packageHandler.updateEntity(eversion, repo);
				}
			}
			if(!provided.containsKey(epackage.getName())) {
				provided.put(eversion.getPkg().getName(), new HashSet<>());
			}
			provided.get(eversion.getPkg().getName()).add(eversion);
		}

		this.performDBClean(repo, provided);
		this.templateHandler.updateAllPackages();
	}

	@Transactional
	public void performDBClean(ERepo repo, Map<String, Set<EPackageVersion>> provided) {
		for(EPackage pkg : this.packageDAO.findList()) {
			this.cleanUpVersions(provided.get(pkg.getName()), pkg.getVersions(), repo);
		}

		for(EPackage emptyPackage : this.packageDAO.findEmpty()) {
			// check if it's used somewhere within configuration files
			for (EFile file : this.fileDAO.findByPackage(emptyPackage)) {
				file.setPkg(null); // remove pkg ref
				this.fileDAO.save(file);
			}
			this.packageDAO.deleteById(emptyPackage.getId());
		}
	}

	private void cleanUpVersions(Set<EPackageVersion> newPackageVersions, Set<EPackageVersion> existing, ERepo currentRepo) {
		if(existing == null) {
			return;
		}
		for(EPackageVersion dbVersion : existing) {
			boolean provided = false;
			if(newPackageVersions != null) {
				for(EPackageVersion newPackageVersion : newPackageVersions) {
					if(newPackageVersion.getId().equals(dbVersion.getId())) {
						provided = true;
						break;
					}
				}
			}

			if(!provided) {
				this.handleNotProvidedVersion(currentRepo, dbVersion);
			}
		}
	}

	private void handleNotProvidedVersion(ERepo currentRepo, EPackageVersion dbVersion) {
		if(this.packageHandler.checkIfInUse(dbVersion, currentRepo)) {
			if(dbVersion.getRepos().size() <= 1) {
				dbVersion.setDeprecated(true);
			}
			this.versionDAO.save(dbVersion);
			return;
		}
		if(dbVersion.getRepos().size() > 0) {
			if(dbVersion.getRepos().stream().anyMatch((e) -> e.equals(currentRepo))) { // TODO NOT?
				//more repos than the current one provide this package -> we keep it but remove the reference for the current repo
				dbVersion.getRepos().remove(currentRepo);
				dbVersion = this.versionDAO.save(dbVersion);
			}
		}
		if(dbVersion.getRepos().size() < 1) {
			this.versionDAO.delete(dbVersion);
		}
	}
}
