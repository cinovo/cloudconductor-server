package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;

import javax.ws.rs.*;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("/package")
public interface IPackage {

	/**
	 * @return set of packages
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Set<Package> get();

	/**
	 * @param pkgName the name of the package
	 * @return the package
	 */
	@GET
	@Path("/{package}")
	@Produces(MediaType.APPLICATION_JSON)
	Package get(@PathParam("package") String pkgName);

	/**
	 * @param pkg the package to save
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void save(Package pkg);


	/**
	 * @param pkgName the name of package to delete
	 */
	@DELETE
	@Path("/{package}")
	void delete(@PathParam("package") String pkgName);

	/**
	 * Get the existing package versions of a package
	 *
	 * @param pkgname the package name
	 * @return collection of package versions
	 */
	@GET
	@Path("/{pkg}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	Set<PackageVersion> getVersions(@PathParam("pkg") String pkgname);

	/**
	 * Add a new version to a package
	 *
	 * @param pkgname        the package name
	 * @param versionContent the version content
	 */
	@PUT
	@Path("/{pkg}")
	@Consumes(MediaType.APPLICATION_JSON)
	void addVersion(@PathParam("pkg") String pkgname, PackageVersion versionContent);

	/**
	 * Delete a version from a package
	 *
	 * @param pkgname the package name
	 * @param version the version
	 */
	@DELETE
	@Path("/{pkg}/{version}")
	void removeVersion(@PathParam("pkg") String pkgname, @PathParam("version") String version);
}
