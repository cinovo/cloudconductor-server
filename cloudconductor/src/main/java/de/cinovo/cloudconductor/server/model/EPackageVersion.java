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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.cinovo.cloudconductor.api.model.INamed;
import de.taimos.dao.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "packageversion", schema = "cloudconductor")
public class EPackageVersion implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private EPackage pkg;
	private String version;
	private Set<EDependency> dependencies = new HashSet<>();
	private Boolean deprecated;
	
	
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
	 * @return the package
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "packageid")
	public EPackage getPkg() {
		return this.pkg;
	}
	
	/**
	 * @param pkg the package to set
	 */
	public void setPkg(EPackage pkg) {
		this.pkg = pkg;
	}
	
	/**
	 * @return the version
	 */
	@Column(nullable = false)
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return the dependencies
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JoinTable(name = "mappingrpmdep", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "rpmid"), inverseJoinColumns = @JoinColumn(name = "dependencyid"))
	public Set<EDependency> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(Set<EDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	@Transient
	@Override
	public String getName() {
		return this.getVersion();
	}
	
	/**
	 * @param name the name
	 */
	public void setName(String name) {
		this.setVersion(name);
	}
	
	/**
	 * @return the deprecated
	 */
	public Boolean getDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(Boolean deprecated) {
		this.deprecated = deprecated;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EPackageVersion)) {
			return false;
		}
		EPackageVersion other = (EPackageVersion) obj;
		if (this.getVersion() == null) {
			return false;
		}
		if (!this.getVersion().equals(other.getVersion())) {
			return false;
		}
		return this.getPkg().equals(other.getPkg());
	}
	
	@Override
	public int hashCode() {
		int val = (this.getVersion() == null) ? 0 : this.getVersion().hashCode();
		int parent = (this.getPkg() == null) ? 0 : this.getPkg().hashCode();
		return val * parent;
	}
}
