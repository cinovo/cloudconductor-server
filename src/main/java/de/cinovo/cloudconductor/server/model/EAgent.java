package de.cinovo.cloudconductor.server.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.taimos.dao.IEntity;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Entity
@Table(name = "agent", schema = "cloudconductor")
public class EAgent implements IEntity<Long> {
	
	private Long id;
	
	private String name;
	
	private EAgentAuthToken token;
	
	
	@Override
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
	 * @return the name of the agent
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the agent-name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return token for agent authentication
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "agentauthtokenid")
	public EAgentAuthToken getToken() {
		return this.token;
	}
	
	/**
	 * @param token the authentication token
	 */
	public void setToken(EAgentAuthToken token) {
		this.token = token;
	}
	
}
