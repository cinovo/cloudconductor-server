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

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.model.ServiceDefaultState;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "servicedefaultstate", schema = "cloudconductor")
public class EServiceDefaultState implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	
	private Long serviceId;
	
	private Long templateId;
	private ServiceState state = ServiceState.STOPPED;
	
	
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
	@Column(name = "serviceid")
	public Long getServiceId() {
		return this.serviceId;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setServiceId(Long service) {
		this.serviceId = service;
	}
	
	/**
	 * @return the host
	 */
	@Column(name = "templateid")
	public Long getTemplateId() {
		return this.templateId;
	}
	
	/**
	 * @param template the template to set
	 */
	public void setTemplateId(Long template) {
		this.templateId = template;
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EServiceDefaultState)) {
			return false;
		}
		EServiceDefaultState other = (EServiceDefaultState) obj;
		if ((this.getId() != null) && (other.getId() != null)) {
			return this.getId().equals(other.getId());
		}
		boolean result = this.serviceId.equals(other.serviceId);
		if (result) {
			result = this.templateId.equals(other.templateId);
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int val = (this.getId() == null) ? 0 : this.getId().hashCode();
		int parent = (this.serviceId == null) ? 0 : this.serviceId.hashCode();
		int parent2 = (this.templateId == null) ? 0 : this.templateId.hashCode();
		return val * (parent + parent2);
	}
	
	/**
	 * @return the api object
	 */
	@Transient
	public ServiceDefaultState toApi() {
		return GenericModelApiConverter.convert(this, ServiceDefaultState.class);
	}
}
