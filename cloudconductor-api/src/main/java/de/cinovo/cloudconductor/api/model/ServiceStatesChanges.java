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
 * The response part of the service update interaction.
 * 
 * @author mhilbert
 */
public class ServiceStatesChanges {
	
	private Set<String> toStart;
	private Set<String> toStop;
	private Set<String> toRestart;
	private Set<ConfigFile> configFiles;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param toStart a list of the init scripts of the services that need to be started
	 * @param toStop a list of the init scripts of the services that need to be stopped
	 * @param toRestart a list of the init scripts of the services that need to be restarted
	 * @param configFileHashes the list of configuration files for the host
	 */
	@JsonCreator
	public ServiceStatesChanges(@JsonProperty("toStart") Set<String> toStart, @JsonProperty("toStop") Set<String> toStop, @JsonProperty("toRestart") Set<String> toRestart, @JsonProperty("configFiles") Set<ConfigFile> configFileHashes) {
		this.toStart = toStart;
		this.toStop = toStop;
		this.toRestart = toRestart;
		this.configFiles = configFileHashes;
	}
	
	/**
	 * @return the toStart
	 */
	public Set<String> getToStart() {
		return this.toStart;
	}
	
	/**
	 * @return the toStop
	 */
	public Set<String> getToStop() {
		return this.toStop;
	}
	
	/**
	 * @return the configFiles
	 */
	public Set<ConfigFile> getConfigFiles() {
		return this.configFiles;
	}
	
	/**
	 * @return the toRestart
	 */
	public Set<String> getToRestart() {
		return this.toRestart;
	}
	
}
