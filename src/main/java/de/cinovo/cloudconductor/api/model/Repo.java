package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.cinovo.cloudconductor.api.interfaces.INamed;

import java.util.Set;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class Repo implements INamed {

	private Long id;
	private String name;
	private String description;
	private Set<RepoMirror> mirrors;
	private Long primaryMirror;
	private Long lastIndex;

	@Override
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
	 * @return the mirrors
	 */
	public Set<RepoMirror> getMirrors() {
		return this.mirrors;
	}

	/**
	 * @param mirrors the mirrors to set
	 */
	public void setMirrors(Set<RepoMirror> mirrors) {
		this.mirrors = mirrors;
	}

	/**
	 * @return the primaryMirror
	 */
	public Long getPrimaryMirror() {
		return this.primaryMirror;
	}

	/**
	 * @param primaryMirror the primaryMirror to set
	 */
	public void setPrimaryMirror(Long primaryMirror) {
		this.primaryMirror = primaryMirror;
	}

	/**
	 * @return the id
	 */
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
	 * @return the lastIndex
	 */
	public Long getLastIndex() {
		return this.lastIndex;
	}

	/**
	 * @param lastIndex the lastIndex to set
	 */
	public void setLastIndex(Long lastIndex) {
		this.lastIndex = lastIndex;
	}
}
