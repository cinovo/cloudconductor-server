package de.cinovo.cloudconductor.server.repo.importer;

import de.cinovo.cloudconductor.api.model.PackageVersion;

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
	 * @param packageVersions the package versions
	 */
	void importVersions(Set<PackageVersion> packageVersions);
	
	/**
	 * @param packageVersions the package versions
	 * @param repoName the repo name
	 */
	void importVersions(Set<PackageVersion> packageVersions, String repoName);
	
}