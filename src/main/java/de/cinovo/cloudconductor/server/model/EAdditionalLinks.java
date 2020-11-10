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

import de.cinovo.cloudconductor.api.model.AdditionalLink;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "additionallinks", schema = "cloudconductor")
public class EAdditionalLinks implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String label;
	private String url;
	
	
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
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the api class
	 */
	@Transient
	public Class<AdditionalLink> getApiClass() {
		return AdditionalLink.class;
	}
	
	/**
	 * @return the api object
	 */
	@Transient
	public AdditionalLink toApi() {
		return new AdditionalLink(this.id, this.label, this.url);
	}
}
