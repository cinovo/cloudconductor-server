package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.security.HashedPasswordUserType;
import de.taimos.dvalin.jaxrs.security.HashedPassword;
import de.taimos.dvalin.jaxrs.security.IUser;
import de.taimos.dvalin.jpa.IEntity;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "user", schema = "cloudconductor")
public class EUser extends AModelApiConvertable<User> implements IEntity<Long>, IUser {

	private static final long serialVersionUID = 9195563439732653226L;

	private Long id;
	private String loginName;
	private String displayName;
	private DateTime registrationDate;
	private boolean active;

	private Set<EUserGroup> userGroup = new HashSet<>();
	private Set<EAuthToken> authTokens;
	private Set<EJWTToken> jwtTokens;

	private Set<EAgent> agents;

	private HashedPassword password;
	private String email;


	@Override
	@Transient
	public String getUsername() {
		return this.getLoginName();
	}

	@Override
	@Transient
	public String[] getRoles() {
		Set<String> roles = new HashSet<>();
		for(EUserGroup eUserGroup : this.userGroup) {
			roles.addAll(eUserGroup.getPermissionsAsString());
		}
		return roles.toArray(new String[roles.size()]);
	}

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
	 * @return the loginName
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the password
	 */
	@Type(type = HashedPasswordUserType.TYPE)
	@Columns(columns = {@Column(name = "roundOffset"), @Column(name = "hash"), @Column(name = "salt")})
	public HashedPassword getPassword() {
		return this.password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(HashedPassword password) {
		this.password = password;
	}

	/**
	 * @return the authTokens
	 */
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	public Set<EAuthToken> getAuthTokens() {
		return this.authTokens;
	}

	/**
	 * @param authTokens the authTokens to set
	 */
	public void setAuthTokens(Set<EAuthToken> authTokens) {
		this.authTokens = authTokens;
	}

	/**
	 * @return the jwtTokens
	 */
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	public Set<EJWTToken> getJwtTokens() {
		return this.jwtTokens;
	}

	/**
	 * @param jwtTokens the jwtTokens to set
	 */
	public void setJwtTokens(Set<EJWTToken> jwtTokens) {
		this.jwtTokens = jwtTokens;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the registrationDate
	 */
	@Type(type = "de.taimos.dvalin.jpa.JodaDateTimeType" )
	public DateTime getRegistrationDate() {
		return this.registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(DateTime registrationDate) {
		this.registrationDate = registrationDate;
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
	 * @return the userGroup
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JoinTable(name = "map_user_usergroup", schema = "cloudconductor", //
			joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "groupid"))
	public Set<EUserGroup> getUserGroup() {
		return this.userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(Set<EUserGroup> userGroup) {
		this.userGroup = userGroup;
	}

	/**
	 * @return the agents
	 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	public Set<EAgent> getAgents() {
		return this.agents;
	}

	/**
	 * @param agents the agents to set
	 */
	public void setAgents(Set<EAgent> agents) {
		this.agents = agents;
	}

	@Override
	@Transient
	public Class<User> getApiClass() {
		return User.class;
	}

	@Override
	@Transient
	public User toApi() {
		User user = new User();
		user.setActive(this.isActive());
		user.setDisplayName(this.getDisplayName());
		user.setEmail(this.getEmail());
		user.setLoginName(this.getLoginName());
		user.setRegistrationDate(this.getRegistrationDate().toDate());
		user.setUserGroups(this.getUserGroup().stream().map(EUserGroup::getName).collect(Collectors.toSet()));
		user.setAuthTokens(this.getAuthTokens().stream().map(EAuthToken::toApi).collect(Collectors.toSet()));
		user.setAgents(this.getAgents().stream().map(EAgent::getName).collect(Collectors.toSet()));
		return user;
	}
}
