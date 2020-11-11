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
import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.HostIdentifier;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "host", schema = "cloudconductor")
public class EHost implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	private Long templateId;
	private Long lastSeen;
	private Long startedUpdate;
	private boolean executedSSH = false;
	private boolean executedFiles = false;
	private boolean executedPkg = false;
	private Long agentId;
	private String uuid;
	
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
	@Override
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
	
	/**
	 * @return the templateid
	 */
	@Column(name="templateid")
	public Long getTemplateId() {
		return this.templateId;
	}
	
	/**
	 * @param templateid the templateid to set
	 */
	public void setTemplateId(Long templateid) {
		this.templateId = templateid;
	}
	
	
	/**
	 * @return the lastSeen
	 */
	public Long getLastSeen() {
		return this.lastSeen;
	}
	
	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(Long lastSeen) {
		this.lastSeen = lastSeen;
	}
	
	/**
	 * @return the onUpdate
	 */
	public Long getStartedUpdate() {
		return this.startedUpdate;
	}
	
	/**
	 * @param onUpdate the onUpdate to set
	 */
	public void setStartedUpdate(Long onUpdate) {
		this.startedUpdate = onUpdate;
	}
	
	/**
	 * @return the executedSSH
	 */
	public boolean getExecutedSSH() {
		return this.executedSSH;
	}
	
	/**
	 * @param executedSSH the executedSSH to set
	 */
	public void setExecutedSSH(boolean executedSSH) {
		this.executedSSH = executedSSH;
	}
	
	/**
	 * @return the executedFiles
	 */
	public boolean getExecutedFiles() {
		return this.executedFiles;
	}
	
	/**
	 * @param executedFiles the executedFiles to set
	 */
	public void setExecutedFiles(boolean executedFiles) {
		this.executedFiles = executedFiles;
	}
	
	/**
	 * @return the executedPkg
	 */
	public boolean getExecutedPkg() {
		return this.executedPkg;
	}
	
	/**
	 * @param executedPkg the executedPkg to set
	 */
	public void setExecutedPkg(boolean executedPkg) {
		this.executedPkg = executedPkg;
	}
	
	/**
	 * @return the Agent running on this Host
	 */
	@Column(name = "agentid")
	public Long getAgentId() {
		return this.agentId;
	}
	
	/**
	 * @param agentId the agent running on this host to set
	 */
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return this.uuid;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @param serviceStateDAO the service state dao
	 * @param agentDAO        the agent dao
	 * @param packageStateDAO the package dao
	 * @param templateDAO     template dao
	 * @return the api object
	 */
	@Transient
	public Host toApi(IServiceStateDAO serviceStateDAO, IAgentDAO agentDAO, IPackageStateDAO packageStateDAO, ITemplateDAO templateDAO) {
		Host api = GenericModelApiConverter.convert(this, Host.class);
		api.setTemplate(templateDAO.findById(this.templateId).getName());
		Map<String, ServiceState> serviceMap = new HashMap<>();
		for (EServiceState service : serviceStateDAO.findByHost(this.id)) {
			serviceMap.put(service.getServiceName(), service.getState());
		}
		api.setServices(serviceMap);
		
		if (this.getAgentId() != null && agentDAO != null) {
			api.setAgent(agentDAO.findName(this.getAgentId()));
		}
		
		Map<String, String> packageMap = new HashMap<>();
		for (EPackageState ps : packageStateDAO.findByHost(this.id)) {
			packageMap.put(ps.getPkgName(), ps.getVersion());
		}
		api.setPackages(packageMap);
		
		return api;
	}
	
	/**
	 * @return the host identifier
	 */
	@Transient
	public HostIdentifier toHostIdentifier() {
		return new HostIdentifier(this.getName(), this.getUuid());
	}
}
