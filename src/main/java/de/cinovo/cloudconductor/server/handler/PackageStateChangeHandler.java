package de.cinovo.cloudconductor.server.handler;

import com.google.common.collect.ArrayListMultimap;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.enums.PackageCommand;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class PackageStateChangeHandler {
	
	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	
	
	/**
	 * 
	 * @param nominal list of package versions which should be installed
	 * @param actual list of package versions which are actually installed
	 * @return multimap including package versions and the command which should be applied to them (e.g install, update, delete)
	 */
	public ArrayListMultimap<PackageCommand, PackageVersion> computePackageDiff(List<EPackageVersion> nominal, Set<EPackageVersion> actual) {
		// Determine which package versions need to be erased and which are to be installed.
		TreeSet<EPackageVersion> toInstall = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toInstall.addAll(nominal);
		toInstall.removeAll(actual);
		TreeSet<EPackageVersion> toErase = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toErase.addAll(actual);
		toErase.removeAll(nominal);
		
		// Resolve the removal of an older version and the installation of a newer one to an updateEntity instruction.
		TreeSet<EPackageVersion> toUpdate = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		for (EPackageVersion i : toInstall) {
			EPackageVersion e = toErase.lower(i);
			if ((e != null) && e.getPkg().getName().equals(i.getPkg().getName())) {
				toErase.remove(e);
				toUpdate.add(i);
			}
		}
		toInstall.removeAll(toUpdate);
		
		// get rid of reserved packages on erase
		Set<EPackageVersion> keep = new HashSet<>();
		EServerOptions eServerOptions = this.serverOptionsDAO.get();
		for (String pkg : eServerOptions.getDisallowUninstall()) {
			for (EPackageVersion erase : toErase) {
				if (erase.getPkg().getName().equals(pkg)) {
					keep.add(erase);
					break;
				}
			}
		}
		toErase.removeAll(keep);
		
		// Convert the lists of package versions to lists of RPM descriptions (RPM name, release, and version).
		ArrayListMultimap<PackageCommand, PackageVersion> result = ArrayListMultimap.create();
		result = this.fillPackageDiff(result, PackageCommand.INSTALL, toInstall);
		result = this.fillPackageDiff(result, PackageCommand.UPDATE, toUpdate);
		result = this.fillPackageDiff(result, PackageCommand.ERASE, toErase);
		return result;
	}
	
	private ArrayListMultimap<PackageCommand, PackageVersion> fillPackageDiff(ArrayListMultimap<PackageCommand, PackageVersion> map, PackageCommand command, Collection<EPackageVersion> packageVersions) {
		for (EPackageVersion pv : packageVersions) {
			PackageVersion apiPV = pv.toApi();
			map.put(command, apiPV);
		}
		return map;
	}
	
}
