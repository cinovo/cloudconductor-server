package de.cinovo.cloudconductor.server.repo.importer;

import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;

import java.util.Set;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public interface IPackageImport {
	
	/**
	 * @param repo
	 * @param packageVersions the package versions
	 */
	void importVersions(ERepo repo, Set<PackageVersion> packageVersions);

}