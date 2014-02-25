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
@Table(name = "serveroptions", schema = "cloudconductor")
public class EServerOptions implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String name;
	
	private String bgcolor;
	
	private boolean allowautoupdate;
	
	private String description;
	
	
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
	 * @return the bgcolor
	 */
	public String getBgcolor() {
		return this.bgcolor;
	}
	
	/**
	 * @param bgcolor the bgcolor to set
	 */
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	
	/**
	 * @return the allowautoupdate
	 */
	public boolean isAllowautoupdate() {
		return this.allowautoupdate;
	}
	
	/**
	 * @param allowautoupdate the allowautoupdate to set
	 */
	public void setAllowautoupdate(boolean allowautoupdate) {
		this.allowautoupdate = allowautoupdate;
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
	
}
