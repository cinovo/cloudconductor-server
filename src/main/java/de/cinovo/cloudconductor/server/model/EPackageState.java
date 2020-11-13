package de.cinovo.cloudconductor.server.model;

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

import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "packagestate", schema = "cloudconductor")
public class EPackageState implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long pkgId;
	private String pkgName;
	
	private Long versionId;
	private String version;
	private Long hostId;
	
	/**
	 * for framework
	 */
	public EPackageState() {
	}
	
	/**
	 * @param version the version
	 * @param host    the host
	 */
	public EPackageState(EPackageVersion version, EHost host) {
		this.hostId = host.getId();
		this.pkgId = version.getPkgId();
		this.pkgName = version.getPkgName();
		
		this.versionId = version.getId();
		this.version = version.getVersion();
	}
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the host
	 */
	@Column(name = "hostid")
	public Long getHostId() {
		return this.hostId;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHostId(Long host) {
		this.hostId = host;
	}
	
	/**
	 * @return the version
	 */
	@Column(name = "rpmid")
	public Long getVersionId() {
		return this.versionId;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersionId(Long version) {
		this.versionId = version;
	}
	
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the pkgId
	 */
	public Long getPkgId() {
		return this.pkgId;
	}
	
	/**
	 * @param pkgId the pkgId to set
	 */
	public void setPkgId(Long pkgId) {
		this.pkgId = pkgId;
	}
	
	/**
	 * @return the pkgName
	 */
	@Column(name="pkgname")
	public String getPkgName() {
		return this.pkgName;
	}
	
	/**
	 * @param pkgName the pkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EPackageState)) {
			return false;
		}
		EPackageState other = (EPackageState) obj;
		if (this.getVersionId() == null) {
			return false;
		}
		if (!this.getVersionId().equals(other.getVersionId())) {
			return false;
		}
		return this.getHostId().equals(other.getHostId());
	}
	
	@Override
	public int hashCode() {
		int val = (this.getVersionId() == null) ? 0 : this.getVersionId().hashCode();
		int parent = (this.getHostId() == null) ? 0 : this.getHostId().hashCode();
		return val * parent;
	}
}
