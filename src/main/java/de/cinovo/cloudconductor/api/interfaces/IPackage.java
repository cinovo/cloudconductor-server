package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Map;
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
	 * @param pkgname the pacakge name
	 * @return map of template-packageVersion pairs
	 */
	@GET
	@Path("/{pkg}/usage")
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, String> getUsage(@PathParam("pkg") String pkgname);

	/**
	 * Get the existing package versions of a repo
	 *
	 * @param repoName the repo name
	 * @return collection of package versions
	 */
	@GET
	@Path("/versions/repo/{repo}")
	@Produces(MediaType.APPLICATION_JSON)
	Set<PackageVersion> getVersionsForRepo(@PathParam("repo") String repoName);
}
