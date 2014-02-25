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


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * The reponse part of the system update interaction.
 * 
 * @author mhilbert
 */
public class PackageStateChanges {
	
	private List<PackageVersion> toInstall;
	private List<PackageVersion> toUpdate;
	private List<PackageVersion> toErase;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param toInstall a list of RPMs to install; the inner array contains the RPM base name and the version string (including release)
	 * @param toUpdate a list of RPMs to update; the inner array contains the RPM base name and the version string (including release)
	 * @param toErase a list of RPMs to erase; the inner array contains the RPM base name and the version string (including release)
	 */
	@JsonCreator
	public PackageStateChanges(@JsonProperty("toInstall") List<PackageVersion> toInstall, @JsonProperty("toUpdate") List<PackageVersion> toUpdate, @JsonProperty("toErase") List<PackageVersion> toErase) {
		this.toInstall = toInstall;
		this.toUpdate = toUpdate;
		this.toErase = toErase;
	}
	
	/**
	 * @return the toInstall
	 */
	public List<PackageVersion> getToInstall() {
		return this.toInstall;
	}
	
	/**
	 * @param toInstall the toInstall to set
	 */
	public void setToInstall(List<PackageVersion> toInstall) {
		this.toInstall = toInstall;
	}
	
	/**
	 * @return the toUpdate
	 */
	public List<PackageVersion> getToUpdate() {
		return this.toUpdate;
	}
	
	/**
	 * @param toUpdate the toUpdate to set
	 */
	public void setToUpdate(List<PackageVersion> toUpdate) {
		this.toUpdate = toUpdate;
	}
	
	/**
	 * @return the toErase
	 */
	public List<PackageVersion> getToErase() {
		return this.toErase;
	}
	
	/**
	 * @param toErase the toErase to set
	 */
	public void setToErase(List<PackageVersion> toErase) {
		this.toErase = toErase;
	}
	
}
