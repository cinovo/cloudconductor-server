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
public class Host implements INamed {
	
	private String name;
	private String description;
	private String template;
	private Set<String> services;
	// TODO add propper hostinfo
	private String hostinfo;
	
	
	/**
	 * @param name the host name
	 * @param descr the description
	 * @param template the template
	 * @param services associated services
	 */
	@JsonCreator
	public Host(@JsonProperty("name") String name, @JsonProperty("description") String descr, @JsonProperty("template") String template, @JsonProperty("services") Set<String> services) {
		this.name = name;
		this.description = descr;
		this.template = template;
		this.services = services;
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
	 * @return the services
	 */
	public Set<String> getServices() {
		return this.services;
	}
	
	/**
	 * @return the hostinfo
	 */
	public String getHostinfo() {
		return this.hostinfo;
	}
	
	/**
	 * @return the template name
	 */
	public String getTemplate() {
		return this.template;
	}
	
}
