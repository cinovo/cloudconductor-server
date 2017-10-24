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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.handler.PackageHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.taimos.restutils.RESTAssert;

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
	public void importVersions(Set<PackageVersion> packageVersions) {
		Map<String, Set<PackageVersion>> repoMap = new HashMap<>();
		for (PackageVersion packageVersion : packageVersions) {
			for (String repo : packageVersion.getRepos()) {
				if (!repoMap.containsKey(repo)) {
					repoMap.put(repo, new HashSet<PackageVersion>());
				}
				repoMap.get(repo).add(packageVersion);
			}
		}
		
		for (Entry<String, Set<PackageVersion>> entry : repoMap.entrySet()) {
			this.importVersions(entry.getValue(), entry.getKey());
		}
	}
	
	@Override
	@Transactional
	public void importVersions(Set<PackageVersion> packageVersions, String repoName) {
		RESTAssert.assertNotEmpty(packageVersions);
		HashMap<String, PackageVersion> provided = new HashMap<>();
		
		for (PackageVersion version : packageVersions) {
			// Retrieve the package for the given version. Create it if it doesn't exist.
			EPackage epackage = this.packageDAO.findByName(version.getName());
			if (epackage == null) { // there is no package for this version yet
				epackage = this.packageHandler.createPackageFromVersion(version);
			}
			
			String name = version.getName();
			String ver = version.getVersion();
			
			// Check if we have this particular package version on record.
			EPackageVersion eversion = this.versionDAO.find(name, ver);
			if (eversion == null) {
				PackageImport.LOGGER.info("Create new package version '" + name + "':'" + ver + "'");
				this.packageHandler.createEntity(version, epackage);
			} else {
				PackageImport.LOGGER.debug("Update existing package version '" + name + "':'" + ver + "'");
				eversion.setPkg(epackage);
				this.packageHandler.updateEntity(eversion, version);
			}
			provided.put(epackage.getName(), version);
		}
		
		this.performDBClean(repoName, provided);
		this.templateHandler.updateAllPackages();
	}
	
	@Transactional
	private void performDBClean(String repoName, HashMap<String, PackageVersion> provided) {
		List<EPackage> inDB = this.packageDAO.findList();
		List<EFile> cfgs = this.fileDAO.findList();
		
		for (EPackage pkg : inDB) {
			if (provided.containsKey(pkg.getName())) {
				// clean up version list
				this.cleanUpVersionUsage(provided.get(pkg.getName()), pkg.getVersions());
				continue;
			}
			
			// check if it's used somewhere within configuration files
			for (EFile cfg : cfgs) {
				if ((cfg.getPkg() != null) && cfg.getPkg().equals(pkg)) {
					cfg.setPkg(null);
					this.fileDAO.save(cfg);
				}
			}
			
			// clean up version list
			PackageVersion packageVersion = new PackageVersion();
			packageVersion.setName(pkg.getName());
			packageVersion.setRepos(new HashSet<String>());
			packageVersion.getRepos().add(repoName);
			boolean cleanUp = this.cleanUpVersionUsage(packageVersion, pkg.getVersions());
			if (cleanUp) {
				PackageImport.LOGGER.info("Clean up: Delete package '" + pkg.getName() + "'");
				this.packageDAO.deleteById(pkg.getId());
			}
		}
	}
	
	private boolean cleanUpVersionUsage(PackageVersion newPackageVersion, Set<EPackageVersion> existing) {
		if (existing == null) {
			PackageImport.LOGGER.info("Package '" + newPackageVersion.getName() + "' is empty! Clean up...");
			return true;
		}
		
		for (EPackageVersion dbVersion : existing) {
			if ((newPackageVersion != null) && (newPackageVersion.getVersion() != null) && newPackageVersion.getVersion().equals(dbVersion.getName())) {
				return false;
			}
			
			// TODO test this with multiple yum repos
			
			if (dbVersion.getRepos().size() > 0) {
				// keep it since other mirrors still reference it
				dbVersion.setDeprecated(false);
				this.versionDAO.save(dbVersion);
				return false;
			}
			
			if (this.packageHandler.checkIfInUse(dbVersion)) {
				PackageImport.LOGGER.info("'" + dbVersion.getPkg().getName() + "':'" + dbVersion.getVersion() + "' still in use, set to deprecated.");
				dbVersion.setDeprecated(true);
				this.versionDAO.save(dbVersion);
				return false;
			}
			
			// delete package version
			PackageImport.LOGGER.info("Delete '" + dbVersion.getPkg().getName() + "':'" + dbVersion.getVersion() + "' because it has no repo");
			this.versionDAO.deleteById(dbVersion.getId());
			return true;
		}
		return true;
	}
	
}
