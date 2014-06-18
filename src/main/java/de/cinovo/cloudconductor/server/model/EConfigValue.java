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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "configvalues", schema = "cloudconductor")
public class EConfigValue implements IVersionized<Long> {
	
	/**	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String template;
	private String service;
	private String configkey;
	private String value;
	private Long version;
	private boolean deleted = false;
	private Long origId;
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Long getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	@Override
	public void setVersion(Long version) {
		this.version = version;
	}
	
	/**
	 * @return the template
	 */
	public String getTemplate() {
		return this.template;
	}
	
	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	/**
	 * @return the service
	 */
	public String getService() {
		return this.service;
	}
	
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}
	
	/**
	 * @return the configkey
	 */
	public String getConfigkey() {
		return this.configkey;
	}
	
	/**
	 * @param configkey the configkey to set
	 */
	public void setConfigkey(String configkey) {
		this.configkey = configkey;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean isDeleted() {
		return this.deleted;
	}
	
	/**
	 * @param deleted the deleted to set
	 */
	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public void setOrigId(Long id) {
		this.origId = id;
	}
	
	@Override
	public Long getOrigId() {
		return this.origId;
	}
	
	@Override
	public IEntity<Long> cloneNew() {
		EConfigValue r = new EConfigValue();
		r.setConfigkey(this.configkey);
		r.setDeleted(this.deleted);
		r.setOrigId(this.origId);
		r.setService(this.service);
		r.setTemplate(this.template);
		r.setValue(this.value);
		r.setVersion(this.version);
		return r;
	}
	
}
