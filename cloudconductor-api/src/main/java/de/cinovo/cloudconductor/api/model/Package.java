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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class Package implements INamed {
	
	private String name;
	private String description;
	private Set<String> rpms;
	
	
	/**
	 * @param name the package name
	 * @param descr the description
	 * @param rpms collection of versions
	 */
	public Package(@JsonProperty("name") String name, @JsonProperty("description") String descr, @JsonProperty("rpms") Set<String> rpms) {
		this.name = name;
		this.description = descr;
		this.rpms = rpms;
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
	public Set<String> getRpms() {
		return this.rpms;
	}
	
}
