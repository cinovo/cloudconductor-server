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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class PackageVersion implements INamed {
	
	private String name;
	private String version;
	private Set<Dependency> dependencies;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param name the base name of the RPM
	 * @param version the version of the RPM
	 * @param dependencies the dependencies of the RPM
	 */
	@JsonCreator
	public PackageVersion(@JsonProperty("name") String name, @JsonProperty("version") String version, @JsonProperty("dependencies") Set<Dependency> dependencies) {
		this.name = name;
		this.version = version;
		this.dependencies = dependencies;
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @return the dependencies
	 */
	public Set<Dependency> getDependencies() {
		return this.dependencies;
	}
}
