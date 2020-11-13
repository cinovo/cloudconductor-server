package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.util.comparators.VersionStringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class PackageStateHandler {
	
	private final Logger logger = LoggerFactory.getLogger(PackageStateHandler.class);
	
	@Autowired
	private IPackageVersionDAO versionDAO;
	
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	@Autowired
	private IRepoDAO repoDAO;
	
	
	/**
	 * @param host         the host which package state should be updated
	 * @param installedPV  the package version to be updated to
	 * @param leftPackages the remaining packages
	 * @return the updated package state or null if no package state was found
	 */
	public EPackageState updateExistingState(EHost host, PackageVersion installedPV, Collection<EPackageState> leftPackages) {
		VersionStringComparator vsc = new VersionStringComparator();
		List<EPackageState> pkgStatesByHost = this.packageStateDAO.findByHost(host.getId());
		for (EPackageState packageState : pkgStatesByHost) {
			if (packageState.getPkgName().equals(installedPV.getName())) {
				int comp = vsc.compare(packageState.getPkgName(), installedPV.getVersion());
				if (comp == 0) {
					break;
				}
				
				// check whether this version of the package already exists
				EPackageVersion pkgVersion = this.versionDAO.find(installedPV.getName(), installedPV.getVersion());
				if (pkgVersion == null) {
					// otherwise create it
					pkgVersion = new EPackageVersion();
					pkgVersion.setPkgId(packageState.getPkgId());
					pkgVersion.setPkgName(packageState.getPkgName());
					pkgVersion.setVersion(installedPV.getVersion());
					pkgVersion.setDeprecated(true);
					for (String repoName : installedPV.getRepos()) {
						ERepo repo = this.repoDAO.findByName(repoName);
						if (repo != null) {
							pkgVersion.getRepos().add(repo.getId());
						}
					}
					pkgVersion = this.versionDAO.save(pkgVersion);
				}
				
				leftPackages.remove(packageState);
				
				// update package state and save it
				packageState.setPkgId(pkgVersion.getPkgId());
				packageState.setPkgName(pkgVersion.getPkgName());
				packageState.setVersionId(pkgVersion.getId());
				packageState.setVersion(pkgVersion.getName());
				return this.packageStateDAO.save(packageState);
			}
		}
		return null;
	}
	
	/**
	 * @param host        the host for which a new package state should be created
	 * @param installedPV the package version which is installed on the host
	 * @param pkg         the package
	 * @return the new package state
	 */
	public EPackageState createMissingState(EHost host, PackageVersion installedPV, EPackage pkg) {
		
		// first check whether this package version does already exist
		EPackageVersion pkgVersion = this.versionDAO.find(installedPV.getName(), installedPV.getVersion());
		
		if (pkgVersion == null) {
			// otherwise create it
			pkgVersion = new EPackageVersion();
			pkgVersion.setPkgId(pkg.getId());
			pkgVersion.setPkgName(pkg.getName());
			pkgVersion.setVersion(installedPV.getVersion());
			pkgVersion.setDeprecated(true);
			
			for (String repoName : installedPV.getRepos()) {
				ERepo repo = this.repoDAO.findByName(repoName);
				if (repo != null) {
					pkgVersion.getRepos().add(repo.getId());
				}
			}
			pkgVersion = this.versionDAO.save(pkgVersion);
		}
		
		// create new package state and save it
		EPackageState newPackageState = new EPackageState();
		newPackageState.setHostId(host.getId());
		newPackageState.setPkgId(pkgVersion.getPkgId());
		newPackageState.setPkgName(pkgVersion.getPkgName());
		newPackageState.setVersionId(pkgVersion.getId());
		newPackageState.setVersion(pkgVersion.getName());
		newPackageState = this.packageStateDAO.save(newPackageState);
		
		return newPackageState;
	}
	
	/**
	 * @param host          the host from which package states should be removed
	 * @param packageStates set of package states to be removed
	 */
	void removePackageState(EHost host, Collection<EPackageState> packageStates) {
		List<EPackageState> statesByHost = this.packageStateDAO.findByHost(host.getId());
		for (EPackageState pkgState : packageStates) {
			if (statesByHost.contains(pkgState)) {
				this.packageStateDAO.delete(pkgState);
				statesByHost.remove(pkgState);
			}
		}
	}
}
