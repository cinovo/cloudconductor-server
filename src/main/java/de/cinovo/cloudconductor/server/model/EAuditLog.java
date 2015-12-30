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

import de.cinovo.cloudconductor.server.model.enums.AuditCategory;
import de.cinovo.cloudconductor.server.model.enums.AuditType;
import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "auditlog", schema = "cloudconductor")
public class EAuditLog implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long timestamp;
	
	private String username;
	
	private String entry;
	
	private AuditCategory category;
	
	private AuditType auditType;
	
	private Long elementId;
	
	private Long origRev;
	
	private Long newRev;
	
	
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
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return this.timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the user
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the user to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the entry
	 */
	public String getEntry() {
		return this.entry;
	}
	
	/**
	 * @param entry the entry to set
	 */
	public void setEntry(String entry) {
		this.entry = entry;
	}
	
	/**
	 * @return the category
	 */
	public AuditCategory getCategory() {
		return this.category;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(AuditCategory category) {
		this.category = category;
	}
	
	/**
	 * @return the auditType
	 */
	public AuditType getAuditType() {
		return this.auditType;
	}
	
	/**
	 * @param auditType the auditType to set
	 */
	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}
	
	/**
	 * @return the elementId
	 */
	public Long getElementId() {
		return this.elementId;
	}
	
	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	
	/**
	 * @return the origRev
	 */
	public Long getOrigRev() {
		return this.origRev;
	}
	
	/**
	 * @param origRev the origRev to set
	 */
	public void setOrigRev(Long origRev) {
		this.origRev = origRev;
	}
	
	/**
	 * @return the newRev
	 */
	public Long getNewRev() {
		return this.newRev;
	}
	
	/**
	 * @param newRev the newRev to set
	 */
	public void setNewRev(Long newRev) {
		this.newRev = newRev;
	}
	
}
