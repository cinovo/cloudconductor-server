package de.cinovo.cloudconductor.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "agentauthtoken", schema = "cloudconductor")
public class EAgentAuthToken implements IEntity<Long>, Comparable<EAgentAuthToken> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String token;
	
	private Long creationDate;
	
	private Long revoked;
	
	private String revokeComment;
	
	
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
	 * @return gets the timestamp of creation of this token
	 */
	public Long getCreationDate() {
		return this.creationDate;
	}
	
	/**
	 * @param creationDate the creation-timestamp of the token to set
	 */
	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * @return the revoked the timestamp this token was revoked, null if never revoked
	 */
	public Long getRevoked() {
		return this.revoked;
	}
	
	/**
	 * @param revoked the revoked to set
	 */
	public void setRevoked(Long revoked) {
		this.revoked = revoked;
	}
	
	/**
	 * @return the revokeComment optional comment for revoke reason
	 */
	public String getRevokeComment() {
		return this.revokeComment;
	}
	
	/**
	 * @param revokeComment the revokeComment to set
	 */
	public void setRevokeComment(String revokeComment) {
		this.revokeComment = revokeComment;
	}
	
	@Override
	public int compareTo(EAgentAuthToken o) {
		if ((this.revoked != null) && (o.revoked != null)) {
			return Long.compare(this.revoked, o.revoked);
		} else if ((this.revoked == null) && (o.revoked != null)) {
			return -1;
		} else if ((this.revoked != null) && (o.revoked == null)) {
			return 1;
		} else {
			if (this.id < o.id) {
				return -1;
			} else if (this.id > o.id) {
				return 1;
			}
			return 0;
		}
	}
	
}
