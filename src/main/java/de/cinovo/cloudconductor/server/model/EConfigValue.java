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

import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jpa.IEntity;

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
@Table(name = "configvalues", schema = "cloudconductor")
public class EConfigValue  implements IEntity<Long> {

	/**     */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String template;
	private String service;
	private String configkey;
	private String value;
	
	/** default constructor */
	public EConfigValue() {
		// nothing to do
	}
	
	/**
	 * @param template	the template name
	 * @param service	the service name
	 * @param key		the configuration key
	 * @param value		the configuration value
	 */
	public EConfigValue(String template, String service, String key, String value) {
		this.template = template;
		this.service = service;
		this.configkey = key;
		this.value = value;
	}
	
	/**
	 * Create config value from api object
	 * @param apiCV the api object to copy
	 */
	public EConfigValue(ConfigValue apiCV) {
		this.template = apiCV.getTemplate();
		this.service = apiCV.getService();
		this.configkey = apiCV.getKey();
		this.configkey = String.valueOf(apiCV.getValue());
	}


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
	
	
	/**
	 * @return the api object
	 */
	@Transient
	public ConfigValue toApi() {
		ConfigValue configValue = GenericModelApiConverter.convert(this, ConfigValue.class);
		configValue.setKey(this.configkey);
		return configValue;
	}
}
