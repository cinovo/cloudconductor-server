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

import de.cinovo.cloudconductor.api.model.INamed;
import de.cinovo.cloudconductor.api.model.Package;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "package", schema = "cloudconductor")
public class EPackage extends AModelApiConvertable<Package> implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	private Set<EPackageVersion> versions;
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
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
	public void setName(final String name) {
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
	 * @return the versions
	 */
	@OneToMany(mappedBy = "pkg", fetch = FetchType.LAZY)
	public Set<EPackageVersion> getVersions() {
		return this.versions;
	}
	
	/**
	 * @param rpms the versions to set
	 */
	public void setVersions(Set<EPackageVersion> rpms) {
		this.versions = rpms;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EPackage)) {
			return false;
		}
		EPackage other = (EPackage) obj;
		if (this.getName() == null) {
			return false;
		}
		return this.getName().equals(other.getName());
	}
	
	@Override
	public int hashCode() {
		return (this.getName() == null) ? 0 : this.getName().hashCode();
	}

	@Override
	@Transient
	public Class<Package> getApiClass() {
		return Package.class;
	}

	@Override
	@Transient
	public Package toApi() {
		Package ret = super.toApi();
		ret.setVersions(this.namedModelToStringSet(this.versions));
		return ret;
	}
}
