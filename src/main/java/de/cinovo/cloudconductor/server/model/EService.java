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
import de.cinovo.cloudconductor.api.model.Service;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "service", schema = "cloudconductor")
public class EService extends AModelApiConvertable<Service> implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;
	private String initScript;
	private List<EPackage> packages = new ArrayList<>();
	
	
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
	@Column(nullable = false)
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
	 * @return the packages
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingsvcpkg", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "svcid"), inverseJoinColumns = @JoinColumn(name = "pkgid"))
	public List<EPackage> getPackages() {
		return this.packages;
	}
	
	/**
	 * @param packages the packages to set
	 */
	public void setPackages(List<EPackage> packages) {
		this.packages = packages;
	}
	
	/**
	 * @return the initScript
	 */
	@Column(nullable = false)
	public String getInitScript() {
		return this.initScript;
	}
	
	/**
	 * @param initScript the initScript to set
	 */
	public void setInitScript(String initScript) {
		this.initScript = initScript;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EService)) {
			return false;
		}
		EService other = (EService) obj;
		if (this.getName().equals(other.getName()) && this.id.equals(other.getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int val = (this.getName() == null) ? 0 : this.getName().hashCode();
		int idVal = (this.getId() == null) ? 0 : this.getId().hashCode();
		return val * idVal;
	}

	@Override
	@Transient
	public Class<Service> getApiClass() {
		return Service.class;
	}

	@Override
	public Service toApi() {
	    Service service = new Service();
	    service.setId(this.id);
	    service.setName(this.name);
	    service.setDescription(this.description);
	    service.setInitScript(this.initScript);
	    service.setPackages(this.packages.stream().map(EPackage::getName).collect(Collectors.toSet()));
	    return service;
	}
}
