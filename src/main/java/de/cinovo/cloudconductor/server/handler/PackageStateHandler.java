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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class PackageStateHandler {
	
	@Autowired
	private IPackageVersionDAO versionDAO;
	
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	@Autowired
	private IRepoDAO repoDAO;
	
	
	/**
	 * @param host the host which package state should be updated
	 * @param installedPV the package version to be updated to
	 * @param leftPackages the remaining packages
	 * @return the updated package state or null if no package state was found
	 */
	public EPackageState updateExistingState(EHost host, PackageVersion installedPV, HashSet<EPackageState> leftPackages) {
		VersionStringComparator vsc = new VersionStringComparator();
		
		for (EPackageState packageState : host.getPackages()) {
			if (packageState.getVersion().getPkg().getName().equals(installedPV.getName())) {
				int comp = vsc.compare(packageState.getVersion().getVersion(), installedPV.getVersion());
				if (comp == 0) {
					break;
				}
				
				// check whether this version of the package already exists
				EPackageVersion rpm = this.versionDAO.find(installedPV.getName(), installedPV.getVersion());
				if (rpm == null) {
					
					// otherwise create it
					rpm = new EPackageVersion();
					rpm.setPkg(packageState.getVersion().getPkg());
					rpm.setVersion(installedPV.getVersion());
					rpm.setDeprecated(true);
					for (String repoName : installedPV.getRepos()) {
						ERepo repo = this.repoDAO.findByName(repoName);
						if (repo != null) {
							rpm.getRepos().add(repo);
						}
					}
					rpm = this.versionDAO.save(rpm);
				}

				leftPackages.remove(packageState);
				
				// update package state and save it
				packageState.setVersion(rpm);
				return this.packageStateDAO.save(packageState);
			}
		}
		return null;
	}
	
	/**
	 * @param host the host for which a new package state should be created
	 * @param installedPV the package version which is installed on the host
	 * @param pkg the package
	 * @return the new package state
	 */
	public EPackageState createMissingState(EHost host, PackageVersion installedPV, EPackage pkg) {
		
		// first check whether this package version does already exist
		EPackageVersion rpm = this.versionDAO.find(installedPV.getName(), installedPV.getVersion());
		
		if (rpm == null) {
			// otherwise create it
			rpm = new EPackageVersion();
			rpm.setPkg(pkg);
			rpm.setVersion(installedPV.getVersion());
			rpm.setDeprecated(true);
			
			for (String repoName : installedPV.getRepos()) {
				ERepo repo = this.repoDAO.findByName(repoName);
				if (repo != null) {
					rpm.getRepos().add(repo);
				}
			}
			rpm = this.versionDAO.save(rpm);
		}
		
		// create new package state and save it
		EPackageState newPackageState = new EPackageState();
		newPackageState.setHost(host);
		newPackageState.setVersion(rpm);
		newPackageState = this.packageStateDAO.save(newPackageState);
		
		return newPackageState;
	}
	
	/**
	 * @param host the host from which package states should be removed
	 * @param packageStates set of package states to be removed
	 */
	public void removePackageState(EHost host, Set<EPackageState> packageStates) {
		for (EPackageState pkgState : packageStates) {
			if (host.getPackages().contains(pkgState)) {
				host.getPackages().remove(pkgState);
				this.packageStateDAO.delete(pkgState);
			}
		}
	}
	
}
