package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.security.HashedPasswordUserType;
import de.taimos.dvalin.jaxrs.security.HashedPassword;
import de.taimos.dvalin.jpa.IEntity;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
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
public class EUser implements IEntity<Long> {
	
	private static final long serialVersionUID = 9195563439732653226L;
	
	private Long id;
	private String loginName;
	private String displayName;
	private DateTime registrationDate;
	private boolean active;
	
	private Set<Long> userGroup = new HashSet<>();
	
	private HashedPassword password;
	private String email;
	
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
	@Type(type = "de.taimos.dvalin.jpa.JodaDateTimeType")
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
	 * @return the user group ids
	 */
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "map_user_usergroup", joinColumns = {@JoinColumn(name = "userid")})
	@Column(name = "groupid")
	public Set<Long> getUserGroup() {
		return this.userGroup;
	}
	
	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(Set<Long> userGroup) {
		this.userGroup = userGroup;
	}
	
	
	/**
	 * @param userGroupDAO the user group dao
	 * @param authTokenDAO the auth token dao
	 * @param agentDAO     the agent dao
	 * @return the api object
	 */
	@Transient
	public User toApi(IUserGroupDAO userGroupDAO, IAuthTokenDAO authTokenDAO, IAgentDAO agentDAO) {
		User user = new User();
		user.setActive(this.isActive());
		user.setDisplayName(this.getDisplayName());
		user.setEmail(this.getEmail());
		user.setLoginName(this.getLoginName());
		user.setRegistrationDate(this.getRegistrationDate().toDate());
		user.setUserGroups(userGroupDAO.findByIds(this.getUserGroup()).stream().map(EUserGroup::getName).collect(Collectors.toSet()));
		user.setAuthTokens(authTokenDAO.findByUser(this.getId()).stream().map(EAuthToken::toApi).collect(Collectors.toSet()));
		user.setAgents(agentDAO.findByUser(this.getId()).stream().map(EAgent::getName).collect(Collectors.toSet()));
		return user;
	}

}
