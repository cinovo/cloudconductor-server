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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private IFileDAO fileDAO;
	@Autowired
	private IPackageVersionDAO versionDAO;

	@Autowired
	private PackageHandler packageHandler;
	@Autowired
	private TemplateHandler templateHandler;


	/**
	 * @param packageVersions the package versions
	 */
	@Override
	@Transactional
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


	@Transactional
	void importVersions(Set<PackageVersion> packageVersions, String repoName) {
		RESTAssert.assertNotEmpty(packageVersions);
		HashMap<String, Set<PackageVersion>> provided = new HashMap<>();

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
				this.packageHandler.createEntity(version, epackage);
			} else {
				PackageImport.LOGGER.debug("Update existing package version '" + name + "':'" + ver + "'");
				eversion.setPkg(epackage);
				this.packageHandler.updateEntity(eversion, version);
			}
			if(!provided.containsKey(epackage.getName())) {
				provided.put(epackage.getName(), new HashSet<>());
			}
			provided.get(epackage.getName()).add(version);
		}

		this.performDBClean(repoName, provided);
		this.templateHandler.updateAllPackages();
	}


	@Transactional
	void performDBClean(String repoName, HashMap<String, Set<PackageVersion>> provided) {
		List<EPackage> inDB = new ArrayList<>(this.packageDAO.findList());
		List<EFile> cfgs = this.fileDAO.findList();

		for(EPackage pkg : inDB) {
			this.cleanUpVersions(provided.get(pkg.getName()), pkg.getVersions(), repoName);
		}

		for(EPackage ePackage : this.packageDAO.findList()) {
			if(ePackage.getVersions() == null || ePackage.getVersions().isEmpty()) {
				// check if it's used somewhere within configuration files
				for(EFile cfg : cfgs) {
					if((cfg.getPkg() != null) && cfg.getPkg().equals(ePackage)) {
						cfg.setPkg(null);
						this.fileDAO.save(cfg);
					}
				}
				this.packageDAO.deleteById(ePackage.getId());
			}
		}
	}


	@Transactional
	void cleanUpVersions(Set<PackageVersion> newPackageVersions, Set<EPackageVersion> existing, String currentRepo) {
		if(existing == null) {
			return;
		}
		for(EPackageVersion dbVersion : existing) {
			boolean provided = false;
			if(newPackageVersions != null) {
				for(PackageVersion newPackageVersion : newPackageVersions) {
					if(dbVersion.getVersion().equals(newPackageVersion.getVersion())) {
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


	@Transactional
	void handleNotProvidedVersion(String currentRepo, EPackageVersion dbVersion) {
		Set<ERepo> toRemove = new HashSet<>();
		for(ERepo eRepo : dbVersion.getRepos()) {
			if(eRepo.getName().equals(currentRepo)) {
				//the dbVersion still references this repo -> get rid of it
				toRemove.add(eRepo);
			}
		}
		dbVersion.getRepos().removeAll(toRemove);
		if(dbVersion.getRepos().size() > 0) {
			this.versionDAO.save(dbVersion);
			return;
		}
		if(this.packageHandler.checkIfInUse(dbVersion)) {
			dbVersion.setDeprecated(true);
			this.versionDAO.save(dbVersion);
			return;
		}

		this.versionDAO.delete(dbVersion);

	}
}