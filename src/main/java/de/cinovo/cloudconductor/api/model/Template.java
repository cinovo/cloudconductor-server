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

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class Template implements INamed {
	
	private String name;
	private String description;
	
	private Set<String> packageServers;
	
	private Map<String, String> packages;
	
	private Set<String> hosts;
	
	private Set<String> sshkeys;
	
	private Set<String> configFiles;
	
	
	/**
	 * @param name the template name
	 * @param description the description
	 * @param packageServers the package server
	 * @param packages the package versions
	 * @param hosts collection of hosts
	 * @param sshkeys collection of ssh keys
	 * @param configFiles collection of config files
	 */
	public Template(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("packageServers") Set<String> packageServers, @JsonProperty("packages") Map<String, String> packages, @JsonProperty("hosts") Set<String> hosts, @JsonProperty("sshekeys") Set<String> sshkeys, @JsonProperty("configfiles") Set<String> configFiles) {
		super();
		this.name = name;
		this.description = description;
		this.setPackageServers(packageServers);
		this.setPackages(packages);
		this.hosts = hosts;
		this.sshkeys = sshkeys;
		this.configFiles = configFiles;
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the hosts
	 */
	public Set<String> getHosts() {
		return this.hosts;
	}
	
	/**
	 * @return the sshkeys
	 */
	public Set<String> getSshkeys() {
		return this.sshkeys;
	}
	
	/**
	 * @return the configFiles
	 */
	public Set<String> getConfigFiles() {
		return this.configFiles;
	}
	
	/**
	 * @return the packageServers
	 */
	public Set<String> getPackageServers() {
		return this.packageServers;
	}
	
	/**
	 * @param packageServers the packageServers to set
	 */
	public void setPackageServers(Set<String> packageServers) {
		this.packageServers = packageServers;
	}
	
	/**
	 * @return the packages
	 */
	public Map<String, String> getPackages() {
		return this.packages;
	}
	
	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Map<String, String> packages) {
		this.packages = packages;
	}
	
}
