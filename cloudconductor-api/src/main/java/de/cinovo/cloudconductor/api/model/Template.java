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
	
	private String yum;
	
	private Map<String, String> rpms;
	
	private Set<String> hosts;
	
	private Set<String> sshkeys;
	
	private Set<String> configFiles;
	
	
	/**
	 * @param name the template name
	 * @param description the description
	 * @param yum the yum path
	 * @param rpms map of versions
	 * @param hosts collection of hosts
	 * @param sshkeys collection of ssh keys
	 * @param configFiles collection of config files
	 */
	public Template(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("yum") String yum, @JsonProperty("rpms") Map<String, String> rpms, @JsonProperty("hosts") Set<String> hosts, @JsonProperty("sshekeys") Set<String> sshkeys, @JsonProperty("configfiles") Set<String> configFiles) {
		super();
		this.name = name;
		this.description = description;
		this.setYum(yum);
		this.rpms = rpms;
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
	 * @return the rpms
	 */
	public Map<String, String> getRpms() {
		return this.rpms;
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
	 * @return the yum
	 */
	public String getYum() {
		return this.yum;
	}
	
	/**
	 * @param yum the yum to set
	 */
	public void setYum(String yum) {
		this.yum = yum;
	}
	
}
