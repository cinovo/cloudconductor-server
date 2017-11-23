package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.enums.UserPermissions;
import de.cinovo.cloudconductor.api.model.UserGroup;
import de.taimos.dvalin.jpa.IEntity;

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
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "usergroup", schema = "cloudconductor")
public class EUserGroup extends AModelApiConvertable<UserGroup> implements IEntity<Long>, Comparable<EUserGroup> {

	private Long id;
	private String name;
	private String description;
	private Set<UserPermissions> permissions = new HashSet<>();

	/**
	 * @return the id
	 */
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
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the permissions
	 */
	@ElementCollection(targetClass = UserPermissions.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "usergroup_permissions", schema = "cloudconductor", joinColumns = @JoinColumn(name = "group_id"))
	@Column(name = "permission")
	public Set<UserPermissions> getPermissions() {
		return this.permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<UserPermissions> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the permissions as an string set
	 */
	@Transient
	@Transactional
	public Set<String> getPermissionsAsString() {
		Set<String> res = new HashSet<>();
		for(UserPermissions p : this.getPermissions()) {
			res.add(p.toString().toUpperCase());
		}
		return res;
	}

	@Override
	@Transient
	public int compareTo(EUserGroup o) {
		return this.id.compareTo(o.getId());
	}

	@Override
	@Transient
	public Class<UserGroup> getApiClass() {
		return UserGroup.class;
	}
}
