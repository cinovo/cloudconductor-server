package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;

import javax.ws.rs.*;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("/package")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IPackage {

	/**
	 * @return set of packages
	 */
	@GET
	Set<Package> get();

	/**
	 * @param pkgName the name of the package
	 * @return the package
	 */
	@GET
	@Path("/{package}")
	Package get(@PathParam("package") String pkgName);


	/**
	 * Get the existing package versions of a package
	 *
	 * @param pkgname the package name
	 * @return collection of package versions
	 */
	@GET
	@Path("/{pkg}/versions")
	Set<PackageVersion> getVersions(@PathParam("pkg") String pkgname);

	/**
	 * @param pkgname the pacakge name
	 * @return map of template-packageVersion pairs
	 */
	@GET
	@Path("/{pkg}/usage")
	Map<String, String> getUsage(@PathParam("pkg") String pkgname);

	/**
	 * Get the existing package versions of a repo
	 *
	 * @param repoName the repo name
	 * @return collection of package versions
	 */
	@GET
	@Path("/versions/repo/{repo}")
	Set<PackageVersion> getVersionsForRepo(@PathParam("repo") String repoName);
}
