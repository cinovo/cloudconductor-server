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

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class Template implements INamed {

	private String name;
	private String description;

	private Set<String> packageServers;

	private Map<String, String> versions;

	private Set<String> hosts;

	private Set<String> sshkeys;

	private Set<String> configFiles;

	private Set<String> directories;

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
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the versions
	 */
	public Map<String, String> getVersions() {
		return this.versions;
	}

	/**
	 * @param versions the versions to set
	 */
	public void setVersions(Map<String, String> versions) {
		this.versions = versions;
	}

	/**
	 * @return the hosts
	 */
	public Set<String> getHosts() {
		return this.hosts;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(Set<String> hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the sshkeys
	 */
	public Set<String> getSshkeys() {
		return this.sshkeys;
	}

	/**
	 * @param sshkeys the sshkeys to set
	 */
	public void setSshkeys(Set<String> sshkeys) {
		this.sshkeys = sshkeys;
	}

	/**
	 * @return the configFiles
	 */
	public Set<String> getConfigFiles() {
		return this.configFiles;
	}

	/**
	 * @param configFiles the configFiles to set
	 */
	public void setConfigFiles(Set<String> configFiles) {
		this.configFiles = configFiles;
	}

	/**
	 * @return the directories
	 */
	public Set<String> getDirectories() {
		return this.directories;
	}

	/**
	 * @param directories the directories to set
	 */
	public void setDirectories(Set<String> directories) {
		this.directories = directories;
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
}
