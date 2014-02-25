package de.cinovo.cloudconductor.server.web.helper;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

/**
 * 
 * Copyright 2012 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
public class ReportPackage implements Comparable<ReportPackage> {
	
	String name;
	String version;
	
	
	/**
	 * @param name the package name
	 * @param version the package version
	 */
	public ReportPackage(String name, String version) {
		this.name = name;
		this.version = version;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		result = (prime * result) + ((this.version == null) ? 0 : this.version.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReportPackage)) {
			return false;
		}
		ReportPackage other = (ReportPackage) obj;
		return this.name.equals(other.name) && this.version.equals(other.version);
	}
	
	@Override
	public int compareTo(ReportPackage o) {
		int compareTo = this.name.compareTo(o.name);
		if (compareTo == 0) {
			return this.version.compareTo(o.version);
		}
		return compareTo;
	}
	
}
