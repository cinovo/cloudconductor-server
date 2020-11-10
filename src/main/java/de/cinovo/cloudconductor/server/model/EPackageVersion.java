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

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SimplePackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "packageversion", schema = "cloudconductor")
public class EPackageVersion implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long pkgId;
	private String pkgName;
	private String version;
	private Set<Long> dependencies = new HashSet<>();
	private Boolean deprecated;
	private Set<Long> repos = new HashSet<>();
	
	
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
	@Column(name = "packageid")
	public Long getPkgId() {
		return this.pkgId;
	}
	
	/**
	 * @param pkg the package to set
	 */
	public void setPkgId(Long pkg) {
		this.pkgId = pkg;
	}
	
	/**
	 * @return the pkgName
	 */
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
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "mappingrpmdep", joinColumns = {@JoinColumn(name = "rpmid")})
	@Column(name = "dependencyid")
	public Set<Long> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(Set<Long> dependencies) {
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
	
	@Transient
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EPackageVersion)) {
			return false;
		}
		EPackageVersion other = (EPackageVersion) obj;
		if ((this.getId() != null) && (other.getId() != null)) {
			return this.getId().equals(other.getId());
		}
		if (this.getVersion() == null) {
			return false;
		}
		if (!this.getVersion().equals(other.getVersion())) {
			return false;
		}
		return this.getPkgId().equals(other.getPkgId());
	}
	
	@Transient
	@Override
	public int hashCode() {
		int val = (this.getVersion() == null) ? 0 : this.getVersion().hashCode();
		int parent = (this.getPkgId() == null) ? 0 : this.getPkgId().hashCode();
		return val * parent;
	}
	
	/**
	 * @return the repos
	 */
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "map_version_repo", joinColumns = {@JoinColumn(name = "versionid")})
	@Column(name = "repoid")
	public Set<Long> getRepos() {
		return this.repos;
	}
	
	/**
	 * @param repos the repos to set
	 */
	public void setRepos(Set<Long> repos) {
		this.repos = repos;
	}
	
	
	/**
	 * @param repoDAO       repo dao
	 * @param dependencyDAO dependency dao
	 * @return the api object
	 */
	@Transient
	public PackageVersion toApi(IRepoDAO repoDAO, IDependencyDAO dependencyDAO) {
		PackageVersion packageVersion = GenericModelApiConverter.convert(this, PackageVersion.class);
		packageVersion.setName(this.pkgName);
		packageVersion.setRepos(new HashSet<>(repoDAO.findNamesByIds(this.repos)));
		packageVersion.setDependencies(new HashSet<>());
		packageVersion.setDependencies(dependencyDAO.findByIds(this.dependencies).stream().map(EDependency::toApi).collect(Collectors.toSet()));
		return packageVersion;
	}
	
	/**
	 * @param repoDAO repo dao
	 * @return the simple api object
	 */
	@Transient
	public SimplePackageVersion toSimpleApi(IRepoDAO repoDAO) {
		List<String> repoNames = repoDAO.findNamesByIds(this.repos);
		return new SimplePackageVersion(this.pkgName, this.getVersion(), repoNames);
	}
}
