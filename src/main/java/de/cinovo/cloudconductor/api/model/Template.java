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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.cinovo.cloudconductor.api.interfaces.INamed;

import java.util.Map;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class Template implements INamed {

	private String name;
	private String description;
	private Set<String> repos;
	private Map<String, String> versions;
	private Set<String> hosts;
	private Boolean autoUpdate;
	private Boolean smoothUpdate;

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
	 * @return the repos
	 */
	public Set<String> getRepos() {
		return this.repos;
	}

	/**
	 * @param repos the repos to set
	 */
	public void setRepos(Set<String> repos) {
		this.repos = repos;
	}

	/**
	 * @return the autoUpdate
	 */
	public Boolean getAutoUpdate() {
		return this.autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(Boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the smoothUpdate
	 */
	public Boolean getSmoothUpdate() {
		return this.smoothUpdate;
	}

	/**
	 * @param smoothUpdate the smoothUpdate to set
	 */
	public void setSmoothUpdate(Boolean smoothUpdate) {
		this.smoothUpdate = smoothUpdate;
	}
}
