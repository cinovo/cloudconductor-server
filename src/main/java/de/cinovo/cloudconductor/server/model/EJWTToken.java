package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.server.model.enums.AuthType;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "jwttoken", schema = "cloudconductor")
public class EJWTToken implements IEntity<Long> {

	private Long id;

	private String token;
	private boolean active;
	private EUser user;
	private AuthType authType;
	private EAuthToken refToken;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the user
	 */
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
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

	/**
	 * @return the authType
	 */
	public AuthType getAuthType() {
		return this.authType;
	}

	/**
	 * @param authType the authType to set
	 */
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	/**
	 * @return the refToken
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "reftoken")
	public EAuthToken getRefToken() {
		return this.refToken;
	}

	/**
	 * @param refToken the refToken to set
	 */
	public void setRefToken(EAuthToken refToken) {
		this.refToken = refToken;
	}
}
