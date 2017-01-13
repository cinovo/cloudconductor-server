package de.cinovo.cloudconductor.server.model;

import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String name;
	
	private EAgentAuthToken token;
	
	private Long tokenAssociationDate;
	
	
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
	@ManyToOne(fetch = FetchType.LAZY)
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
	
	/**
	 * @return the tokenAssociationDate the timestamp where the current token was last associated with
	 */
	public Long getTokenAssociationDate() {
		return this.tokenAssociationDate;
	}
	
	/**
	 * @param tokenAssociationDate the tokenAssociationDate to set
	 */
	public void setTokenAssociationDate(Long tokenAssociationDate) {
		this.tokenAssociationDate = tokenAssociationDate;
	}
	
}
