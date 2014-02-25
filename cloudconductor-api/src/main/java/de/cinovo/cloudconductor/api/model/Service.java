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

import de.cinovo.cloudconductor.api.ServiceState;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class Service implements INamed {
	
	private String name;
	private String description;
	private String initScript;
	private ServiceState state;
	private Set<String> packages;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param name the name of the service
	 * @param description a description of this service
	 * @param initScript the name of the init script of this service
	 * @param state the serviceState
	 * @param pkgs the packages
	 */
	@JsonCreator
	public Service(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("initScript") String initScript, @JsonProperty("state") ServiceState state, @JsonProperty("pkgs") Set<String> pkgs) {
		this.name = name;
		this.description = description;
		this.initScript = initScript;
		this.setState(state);
		this.packages = pkgs;
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
	 * @return the initScript
	 */
	public String getInitScript() {
		return this.initScript;
	}
	
	/**
	 * @return the state
	 */
	public ServiceState getState() {
		return this.state;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(ServiceState state) {
		this.state = state;
	}
	
	/**
	 * @return the packages
	 */
	public Set<String> getPackages() {
		return this.packages;
	}
	
}
