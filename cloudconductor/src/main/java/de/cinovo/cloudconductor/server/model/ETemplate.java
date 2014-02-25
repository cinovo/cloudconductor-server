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

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "template", schema = "cloudconductor")
public class ETemplate implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	
	private Set<EPackageVersion> rpms;
	
	private Set<EHost> hosts;
	
	private Set<ESSHKey> sshkeys;
	
	private Set<EFile> configFiles;
	
	private EYumServer yum;
	
	private Boolean autoUpdate;
	
	
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
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the configFiles
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingfiletemplate", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "fileid"))
	public Set<EFile> getConfigFiles() {
		return this.configFiles;
	}
	
	/**
	 * @param configFiles the configFiles to set
	 */
	public void setConfigFiles(Set<EFile> configFiles) {
		this.configFiles = configFiles;
	}
	
	/**
	 * @return the hosts
	 */
	@OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
	public Set<EHost> getHosts() {
		return this.hosts;
	}
	
	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(Set<EHost> hosts) {
		this.hosts = hosts;
	}
	
	/**
	 * @return the rpms
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingrpmtemplate", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "rpmid"))
	public Set<EPackageVersion> getRPMs() {
		return this.rpms;
	}
	
	/**
	 * @param rpms the rpms to set
	 */
	public void setRPMs(Set<EPackageVersion> rpms) {
		this.rpms = rpms;
	}
	
	/**
	 * @return the sshkeys
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingtemplatesshkey", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "sshkeyid"))
	public Set<ESSHKey> getSshkeys() {
		return this.sshkeys;
	}
	
	/**
	 * @param sshkeys the sshkeys to set
	 */
	public void setSshkeys(Set<ESSHKey> sshkeys) {
		this.sshkeys = sshkeys;
	}
	
	/**
	 * @return the yum server
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@JoinColumn(name = "yum")
	public EYumServer getYum() {
		return this.yum;
	}
	
	/**
	 * @return the yum
	 */
	@Transient
	public String getYumPath() {
		return this.yum.getYumPath();
	}
	
	/**
	 * @param yum the yum to set
	 */
	public void setYum(EYumServer yum) {
		this.yum = yum;
	}
	
	/**
	 * @return the autoUpdate
	 */
	public Boolean getAutoUpdate() {
		return this.autoUpdate;
	}
	
	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(Boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ETemplate)) {
			return false;
		}
		ETemplate other = (ETemplate) obj;
		if (this.getName() == null) {
			return false;
		}
		return this.getName().equals(other.getName());
	}
	
	@Override
	public int hashCode() {
		return (this.getName() == null) ? 0 : this.getName().hashCode();
	}
}
