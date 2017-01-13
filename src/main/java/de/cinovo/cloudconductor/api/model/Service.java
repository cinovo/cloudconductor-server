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

import de.cinovo.cloudconductor.api.ServiceState;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class Service implements INamed {

	private Long id;
	private String name;
	private String description;
	private String initScript;
	private ServiceState state = ServiceState.STOPPED;
	private Set<String> packages;


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
	 * @return the initScript
	 */
	public String getInitScript() {
		return this.initScript;
	}

	/**
	 * @param initScript the initScript to set
	 */
	public void setInitScript(String initScript) {
		this.initScript = initScript;
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

	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Set<String> packages) {
		this.packages = packages;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
