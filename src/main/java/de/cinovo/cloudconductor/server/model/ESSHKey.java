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

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "sshkey", schema = "cloudconductor")
public class ESSHKey implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String keycontent;
	private String owner;
	private String username;
	private Long lastChangedDate;
	private List<Long> templates;
	
	
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
	 * @return the keycontent
	 */
	@Column(nullable = false)
	public String getKeycontent() {
		return this.keycontent;
	}
	
	/**
	 * @param keycontent the keycontent to set
	 */
	public void setKeycontent(String keycontent) {
		this.keycontent = keycontent;
	}
	
	/**
	 * @return the owner
	 */
	@Column(nullable = false)
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the user name of this ssh key
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the user name for this ssh key to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Transient
	@Override
	public String getName() {
		return this.getOwner();
	}
	
	/**
	 * @param name the name
	 */
	public void setName(String name) {
		this.setOwner(name);
	}
	
	/**
	 * @return timestamp of the last change
	 */
	public Long getLastChangedDate() {
		return this.lastChangedDate;
	}
	
	/**
	 * @param lastChangedDate the timestamp of the last change to set
	 */
	public void setLastChangedDate(Long lastChangedDate) {
		this.lastChangedDate = lastChangedDate;
	}
	
	/**
	 * @return list of templates this ssh key belongs to
	 */
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "mappingtemplatesshkey", joinColumns = {@JoinColumn(name = "sshkeyid")})
	@Column(name = "templateid")
	public List<Long> getTemplates() {
		return this.templates;
	}
	
	/**
	 * @param templates list of templates to set
	 */
	public void setTemplates(List<Long> templates) {
		this.templates = templates;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ESSHKey)) {
			return false;
		}
		ESSHKey other = (ESSHKey) obj;
		if ((this.getOwner() == null) || (this.getId() == null)) {
			return false;
		}
		return this.owner.equals(other.getOwner()) && this.id.equals(other.getId());
	}
	
	@Override
	public int hashCode() {
		int val = (this.getOwner() == null) ? 0 : this.getOwner().hashCode();
		int idVal = (this.getId() == null) ? 0 : this.getId().hashCode();
		return val * idVal;
	}
	
	
	/**
	 * @param templateDAO the templateDAO
	 * @return the api object
	 */
	@Transient
	public SSHKey toApi(ITemplateDAO templateDAO) {
		SSHKey apiKey = new SSHKey(this.owner, this.keycontent);
		
		if (this.lastChangedDate != null) {
			apiKey.setLastChanged(new Date(this.lastChangedDate));
		}
		
		apiKey.setUsername(this.username);
		
		List<String> templateNames = new ArrayList<>();
		for (ETemplate t : templateDAO.findByIds(this.getTemplates())) {
			templateNames.add(t.getName());
		}
		apiKey.setTemplates(templateNames);
		
		return apiKey;
	}
	
}
