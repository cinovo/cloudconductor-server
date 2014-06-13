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

import de.cinovo.cloudconductor.api.ServiceState;
import de.taimos.dao.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "servicedefaultstate", schema = "cloudconductor")
public class EServiceDefaultState implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	
	private EService service;
	
	private ETemplate template;
	private ServiceState state;
	
	
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
	 * @return the service
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceid")
	public EService getService() {
		return this.service;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(EService service) {
		this.service = service;
	}
	
	/**
	 * @return the host
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
	 * @return the state
	 */
	public ServiceState getState() {
		return this.state;
	}
	
	/**
	 * @param state the state to set
	 */
	public void setState(ServiceState state) {
		this.state = state;
	}
	
}
