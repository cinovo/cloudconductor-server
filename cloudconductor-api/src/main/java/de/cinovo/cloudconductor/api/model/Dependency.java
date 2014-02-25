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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.cinovo.cloudconductor.api.DependencyType;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * A dependency.
 * 
 * @author psigloch
 */
public class Dependency implements INamed {
	
	private String name;
	private String version;
	private String operator;
	private DependencyType type;
	
	
	/**
	 * @param name the name of the RPM
	 * @param version the version of the RPM
	 * @param operator the operator (>, >=, ...)
	 * @param type the dependency type as string
	 */
	@JsonCreator
	public Dependency(@JsonProperty("name") String name, @JsonProperty("version") String version, @JsonProperty("operator") String operator, @JsonProperty("type") String type) {
		this.name = name;
		this.version = version;
		this.operator = operator;
		this.type = DependencyType.valueOf(type);
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
	 * @return the operator
	 */
	public String getOperator() {
		return this.operator;
	}
	
	/**
	 * @return the type
	 */
	public DependencyType getType() {
		return this.type;
	}
	
}
