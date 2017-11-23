package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.model.AuthToken;
import de.taimos.dvalin.jpa.IEntity;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "authtoken", schema = "cloudconductor")
public class EAuthToken extends AModelApiConvertable<AuthToken> implements IEntity<Long>, Comparable<EAuthToken> {
	private static final long serialVersionUID = 1L;

	private Long id;
	private EUser user;
	private String token;
	private DateTime creationDate;
	private DateTime revokeDate;

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
	 * @return the creationDate
	 */
	@Type(type = "de.taimos.dvalin.jpa.JodaDateTimeType" )
	public DateTime getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(DateTime creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the revokeDate
	 */
	@Type(type = "de.taimos.dvalin.jpa.JodaDateTimeType" )
	public DateTime getRevokeDate() {
		return this.revokeDate;
	}

	/**
	 * @param revokeDate the revokeDate to set
	 */
	public void setRevokeDate(DateTime revokeDate) {
		this.revokeDate = revokeDate;
	}

	@Override
	public int compareTo(EAuthToken o) {
		if((this.revokeDate != null) && (o.revokeDate != null)) {
			return this.revokeDate.compareTo(o.revokeDate);
		}
		if((this.revokeDate == null) && (o.revokeDate != null)) {
			return -1;
		}
		if((this.revokeDate != null) && (o.revokeDate == null)) {
			return 1;
		}
		return this.id.compareTo(o.id);
	}

	@Override
	@Transient
	public Class<AuthToken> getApiClass() {
		return AuthToken.class;
	}

	@Override
	@Transient
	public AuthToken toApi() {
		AuthToken token = new AuthToken();
		token.setCreationDate(this.creationDate.toDate());
		if(this.revokeDate != null) {
			token.setRevoked(this.revokeDate.toDate());
		}
		token.setToken(this.token);
		return token;
	}
}
