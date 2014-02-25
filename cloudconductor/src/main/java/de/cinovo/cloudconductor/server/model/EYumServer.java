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
@Table(name = "yumserver", schema = "cloudconductor")
public class EYumServer implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String yumPath;
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
	 * @return the yumpath
	 */
	public String getYumPath() {
		return this.yumPath;
	}
	
	/**
	 * @param yumPath the yumpath to set
	 */
	public void setYumPath(String yumPath) {
		this.yumPath = yumPath;
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EYumServer)) {
			return false;
		}
		EYumServer other = (EYumServer) obj;
		
		if (this.getYumPath().equals(other.getYumPath()) && this.id.equals(other.getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int val = (this.getYumPath() == null) ? 0 : this.getYumPath().hashCode();
		int idVal = (this.getId() == null) ? 0 : this.getId().hashCode();
		return val * idVal;
	}
	
}
