package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.enums.PackageCommand;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class PackageStateChangeHandler {

	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IRepoDAO repoDAO;

	/**
	 * @param host the host
	 * @return the package state changes
	 */
	public PackageStateChanges computePackageDiff(EHost host) {
		return this.computePackageDiff(host, host.getTemplateid());
	}

	/**
	 * @param host       the host
	 * @param templateId the template to compare with
	 * @return the package state changes
	 */
	private PackageStateChanges computePackageDiff(EHost host, Long templateId) {
		// Compute instruction lists (install/updateEntity/erase) from difference between packages actually installed packages that
		// should be installed.
		Set<EPackageVersion> actual = new HashSet<>();
		for(EPackageState state : host.getPackages()) {
			actual.add(state.getVersion());
		}
		return this.computePackageDiff(templateId, actual);
	}

	/**
	 * @param templateId the referenced template
	 * @param actual     list of package versions which are actually installed
	 * @return multimap including package versions and the command which should be applied to them (e.g install, update, delete)
	 */
	public PackageStateChanges computePackageDiff(Long templateId, Set<EPackageVersion> actual) {
		List<EPackageVersion> nominal = this.packageVersionDAO.findByTemplate(templateId);
		TreeSet<EPackageVersion> toInstall = this.findInstalls(actual, nominal);
		TreeSet<EPackageVersion> toErase = this.findDeletes(actual, nominal, this.repoDAO.findByTemplate(templateId));
		TreeSet<EPackageVersion> toUpdate = this.findUpdates(toInstall, toErase);

		// Convert the lists of package versions to lists of RPM descriptions (RPM name, release, and version).
		PackageStateChanges result = new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		result = this.fillPackageDiff(result, PackageCommand.INSTALL, toInstall);
		result = this.fillPackageDiff(result, PackageCommand.UPDATE, toUpdate);
		result = this.fillPackageDiff(result, PackageCommand.ERASE, toErase);
		return result;
	}

	private TreeSet<EPackageVersion> findInstalls(Set<EPackageVersion> actual, List<EPackageVersion> nominal) {
		// Determine which package versions need to be erased and which are to be installed.
		TreeSet<EPackageVersion> toInstall = new TreeSet<>(new PackageVersionComparator());
		toInstall.addAll(nominal);
		toInstall.removeAll(actual);
		return toInstall;
	}

	private TreeSet<EPackageVersion> findDeletes(Set<EPackageVersion> actual, List<EPackageVersion> nominal, List<ERepo> repos) {
		TreeSet<EPackageVersion> toErase = new TreeSet<>(new PackageVersionComparator());
		toErase.addAll(actual);
		toErase.removeAll(nominal);

		// get rid of reserved packages on erase
		Set<EPackageVersion> keep = new HashSet<>();
		EServerOptions eServerOptions = this.serverOptionsDAO.get();
		for(String pkg : eServerOptions.getDisallowUninstall()) {
			for(EPackageVersion erase : toErase) {
				if(erase.getPkg().getName().trim().equalsIgnoreCase(pkg.trim())) {
					keep.add(erase);
					break;
				}
			}
		}
		//keep packages from an repository not provided in the template
		for(EPackageVersion erase : toErase) {
			if(Collections.disjoint(erase.getRepos(), repos)) {
				keep.add(erase);
			}
		}
		toErase.removeAll(keep);

		return toErase;
	}

	private TreeSet<EPackageVersion> findUpdates(TreeSet<EPackageVersion> toInstall, TreeSet<EPackageVersion> toErase) {
		// Resolve the removal of an older version and the installation of a newer one to an updateEntity instruction.
		TreeSet<EPackageVersion> toUpdate = new TreeSet<>(new PackageVersionComparator());
		for(EPackageVersion i : toInstall) {
			EPackageVersion e = toErase.lower(i);
			if((e != null) && e.getPkg().getName().equals(i.getPkg().getName())) {
				toErase.remove(e);
				toUpdate.add(i);
			}
		}
		toInstall.removeAll(toUpdate);
		return toUpdate;
	}

	private PackageStateChanges fillPackageDiff(PackageStateChanges changes, PackageCommand command, Collection<EPackageVersion> packageVersions) {
		for(EPackageVersion pv : packageVersions) {
			PackageVersion apiPV = pv.toApi();
			switch(command) {
				case INSTALL:
					changes.getToInstall().add(apiPV);
					break;
				case UPDATE:
					changes.getToUpdate().add(apiPV);
					break;
				case ERASE:
					changes.getToErase().add(apiPV);
					break;
			}
		}
		return changes;
	}

}
