package de.cinovo.cloudconductor.server.security;

import com.nimbusds.jwt.JWTClaimsSet;
import de.taimos.dvalin.jaxrs.security.IUser;

import java.util.Date;
import java.util.UUID;

/**
 * Copyright 2020 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class CCAuthenticatedUser implements IUser {
	private static final String CLAIM_USERNAME = "preferred_username";
	private static final String CLAIM_DISPLAY_NAME = "name";
	private static final String CLAIM_ROLES = "roles";
	private static final String UNIQUE_USER_ID = "unique_user_id";
	
	private String id;
	private String username;
	private String displayName;
	private String[] roles;
	
	/**
	 *
	 */
	CCAuthenticatedUser() {
		//
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @param id the id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String[] getRoles() {
		return this.roles.clone();
	}
	
	/**
	 * @param roles the roles
	 */
	public void setRoles(String[] roles) {
		this.roles = roles.clone();
	}
	
	/**
	 * @return the display name
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * @param displayName the display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @param issuer the issuer
	 * @param expiry the expiry date
	 * @return the jwt claim set
	 */
	public JWTClaimsSet toClaimSet(String issuer, Date expiry) {
		JWTClaimsSet.Builder b = new JWTClaimsSet.Builder();
		b.issuer(issuer);
		b.expirationTime(expiry);
		b.subject(this.id);
		b.claim(CCAuthenticatedUser.CLAIM_USERNAME, this.username);
		b.claim(CCAuthenticatedUser.CLAIM_DISPLAY_NAME, this.displayName);
		b.claim(CCAuthenticatedUser.CLAIM_ROLES, this.roles);
		b.claim(CCAuthenticatedUser.UNIQUE_USER_ID, UUID.randomUUID().toString());
		return b.build();
	}
}
