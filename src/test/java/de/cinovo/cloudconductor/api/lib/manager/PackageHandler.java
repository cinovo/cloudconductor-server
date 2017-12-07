package de.cinovo.cloudconductor.api.lib.manager;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class PackageHandler extends DefaultRestHandler<Package> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public PackageHandler(String cloudconductorUrl, String token) {
		super(cloudconductorUrl, token);
	}
	

	@Override
	protected String getDefaultPath() {
		return "/package";
	}
	
	@Override
	protected Class<Package> getAPIClass() {
		return Package.class;
	}
	
	/**
	 * @param packageName the package name
	 * @return set of available versions
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<PackageVersion> getRPMS(String packageName) throws CloudConductorException {
		String path = this.pathGenerator("/{pkg}/versions", packageName);
		return (Set<PackageVersion>) this._get(path, this.getSetType(PackageVersion.class));
	}
	
	/**
	 * @param packageName the package name
	 * @param version the version
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addRPM(String packageName, PackageVersion version) throws CloudConductorException {
		String path = this.pathGenerator("/{pkg}/versions/{version}", packageName, version.getVersion());
		this._put(path, version);
	}
	
	/**
	 * @param packageName the package name
	 * @param version the version
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removeRPM(String packageName, String version) throws CloudConductorException {
		String path = this.pathGenerator("/{pkg}/versions/{version}", packageName, version);
		this._delete(path);
	}
}
