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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
@Table(name = "host", schema = "cloudconductor")
public class EHost implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	private ETemplate template;
	private Long lastSeen;
	private Set<EServiceState> services = new HashSet<>();
	private Set<EPackageState> packages = new HashSet<>();
	
	
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
	 * @return the services
	 */
	@OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
	public Set<EServiceState> getServices() {
		return this.services;
	}
	
	/**
	 * @param services the services to set
	 */
	public void setServices(Set<EServiceState> services) {
		this.services = services;
	}
	
	/**
	 * @return the packages
	 */
	@OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
	public Set<EPackageState> getPackages() {
		return this.packages;
	}
	
	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Set<EPackageState> packages) {
		this.packages = packages;
	}
	
	/**
	 * @return the template
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "templateid")
	public ETemplate getTemplate() {
		return this.template;
	}
	
	/**
	 * @param template the template to set
	 */
	public void setTemplate(ETemplate template) {
		this.template = template;
	}
	
	/**
	 * @return the lastSeen
	 */
	public Long getLastSeen() {
		return this.lastSeen;
	}
	
	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(Long lastSeen) {
		this.lastSeen = lastSeen;
	}
	
}
