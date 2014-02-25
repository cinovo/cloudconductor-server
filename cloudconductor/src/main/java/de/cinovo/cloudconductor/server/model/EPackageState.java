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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.taimos.dao.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "packagestate", schema = "cloudconductor")
public class EPackageState implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	
	private EPackageVersion version;
	
	private EHost host;
	
	
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
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "hostid")
	public EHost getHost() {
		return this.host;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHost(EHost host) {
		this.host = host;
	}
	
	/**
	 * @return the version
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "rpmid")
	public EPackageVersion getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(EPackageVersion version) {
		this.version = version;
	}
	
}
