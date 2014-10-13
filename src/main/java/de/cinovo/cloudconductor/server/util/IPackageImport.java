package de.cinovo.cloudconductor.server.util;

import java.util.Set;

import de.cinovo.cloudconductor.api.model.PackageVersion;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public interface IPackageImport {

	void importVersions(Set<PackageVersion> rpms);

}