package de.cinovo.cloudconductor.api.model;

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

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class PackageVersion implements INamed {

	private String name;
	private String version;
	private Set<Dependency> dependencies;
	private Set<String> packageServerGroup;


	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the dependencies
	 */
	public Set<Dependency> getDependencies() {
		return this.dependencies;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(Set<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * @return the packageServerGroup
	 */
	public Set<String> getPackageServerGroup() {
		return this.packageServerGroup;
	}

	/**
	 * @param packageServerGroup the packageServerGroup to set
	 */
	public void setPackageServerGroup(Set<String> packageServerGroup) {
		this.packageServerGroup = packageServerGroup;
	}
}
