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
import javax.persistence.OneToOne;
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
@Table(name = "filedata", schema = "cloudconductor")
public class EFileData implements IVersionized<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private EFile parent;
	private String data;
	
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
	
	/**
	 * @return the parent
	 */
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fileid")
	public EFile getParent() {
		return this.parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(EFile parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the data
	 */
	public String getData() {
		return this.data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
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
	
	@Override
	public IEntity<Long> cloneNew() {
		EFileData r = new EFileData();
		r.setData(this.data);
		r.setDeleted(this.deleted);
		r.setOrigId(this.origId);
		r.setParent(this.parent);
		r.setVersion(this.version);
		return r;
	}
}
