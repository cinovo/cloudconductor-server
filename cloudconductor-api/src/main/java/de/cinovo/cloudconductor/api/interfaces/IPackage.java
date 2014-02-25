package de.cinovo.cloudconductor.api.interfaces;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.PKG)
public interface IPackage extends IDefaultApi<Package> {
	
	/**
	 * Get the existing package versions of a package
	 * 
	 * @param pkgname the package name
	 * @return collection of package versions
	 */
	@GET
	@Path(IRestPath.PKG_VERSION)
	@Produces(MediaType.APPLICATION_JSON)
	public PackageVersion[] getRPMS(@PathParam(IRestPath.VAR_PKG) String pkgname);
	
	/**
	 * Add a new version to a package
	 * 
	 * @param pkgname the package name
	 * @param version the version
	 * @param versionContent the version content
	 */
	@PUT
	@Path(IRestPath.PKG_VERSION_SINGLE)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addRPM(@PathParam(IRestPath.VAR_PKG) String pkgname, @PathParam(IRestPath.VAR_VERSION) String version, PackageVersion versionContent);
	
	/**
	 * Delete a version from a package
	 * 
	 * @param pkgname the package name
	 * @param version the version
	 */
	@DELETE
	@Path(IRestPath.PKG_VERSION_SINGLE)
	public void removeRPM(@PathParam(IRestPath.VAR_PKG) String pkgname, @PathParam(IRestPath.VAR_VERSION) String version);
}
