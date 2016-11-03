package de.cinovo.cloudconductor.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

import de.taimos.dao.IEntity;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Entity
@Table(name = "agentauthtoken", schema = "cloudconductor")
public class EAgentAuthToken implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String token;
	
	private DateTime creationDate;
	
	
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
	 * @return the AuthToken to get
	 */
	public String getToken() {
		return this.token;
	}
	
	/**
	 * @param token to set AuthToken
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * @return gets the Date of creation of this token
	 */
	public DateTime getCreationDate() {
		return this.creationDate;
	}
	
	/**
	 * @param creationDate the creation-date of the token to set
	 */
	public void setCreationDate(DateTime creationDate) {
		this.creationDate = creationDate;
	}
	
}
