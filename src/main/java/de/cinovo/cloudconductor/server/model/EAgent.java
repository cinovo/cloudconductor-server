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
	private EUser user;

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
	 * @return the user
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	public EUser getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(EUser user) {
		this.user = user;
	}
}
