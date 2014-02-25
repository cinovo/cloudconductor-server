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
 * The request part of the system update interaction.
 * 
 * @author mhilbert
 */
public class PackageState {
	
	private List<PackageVersion> installedRpms;
	
	
	/**
	 * Class constructor.
	 * 
	 * @param installedRpms the installed RPMs; the inner array contains the RPM base name and the version string (including release)
	 */
	@JsonCreator
	public PackageState(@JsonProperty("installedRpms") List<PackageVersion> installedRpms) {
		this.installedRpms = installedRpms;
	}
	
	/**
	 * @return the installedRpms
	 */
	public List<PackageVersion> getInstalledRpms() {
		return this.installedRpms;
	}
}
