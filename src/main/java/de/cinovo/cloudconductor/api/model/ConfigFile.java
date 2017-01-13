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


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigFile implements INamed {
	
	private String name;
	private String pkg;
	private String targetPath;
	private String owner;
	private String group;
	private String fileMode;
	private boolean isTemplate;
	private boolean isReloadable;
	private String checksum;
	private Set<String> dependentServices;
	
	

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return this.targetPath;
	}
	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * @return the fileMode
	 */
	public String getFileMode() {
		return this.fileMode;
	}
	
	/**
	 * @return the isTemplate
	 */
	@JsonProperty("isReloadable")
	public boolean isReloadable() {
		return this.isReloadable;
	}
	
	/**
	 * @return the isTemplate
	 */
	@JsonProperty("isTemplate")
	public boolean isTemplate() {
		return this.isTemplate;
	}
	
	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return this.checksum;
	}
	
	/**
	 * @return the pkg
	 */
	public String getPkg() {
		return this.pkg;
	}
	
	/**
	 * @return the dependentServices
	 */
	public Set<String> getDependentServices() {
		return this.dependentServices;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param pkg the pkg to set
	 */
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	/**
	 * @param targetPath the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @param fileMode the fileMode to set
	 */
	public void setFileMode(String fileMode) {
		this.fileMode = fileMode;
	}

	/**
	 * @param template the isTemplate to set
	 */
	public void setTemplate(boolean template) {
		this.isTemplate = template;
	}

	/**
	 * @param reloadable the isReloadable to set
	 */
	public void setReloadable(boolean reloadable) {
		this.isReloadable = reloadable;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * @param dependentServices the dependentServices to set
	 */
	public void setDependentServices(Set<String> dependentServices) {
		this.dependentServices = dependentServices;
	}
}
