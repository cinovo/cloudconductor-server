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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "sshkey", schema = "cloudconductor")
public class ESSHKey extends AModelApiConvertable<SSHKey> implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String keycontent;
	private String owner;
	
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ESSHKey)) {
			return false;
		}
		ESSHKey other = (ESSHKey) obj;
		if ((this.getOwner() == null) || (this.getId() == null)) {
			return false;
		}
		if (this.owner.equals(other.getOwner()) && this.id.equals(other.getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int val = (this.getOwner() == null) ? 0 : this.getOwner().hashCode();
		int idVal = (this.getId() == null) ? 0 : this.getId().hashCode();
		return val * idVal;
	}
	
	@Override
	@Transient
	public Class<SSHKey> getApiClass() {
		return SSHKey.class;
	}
	
	@Override
	public SSHKey toApi() {
		return new SSHKey(this.owner, this.keycontent);
	}
}
