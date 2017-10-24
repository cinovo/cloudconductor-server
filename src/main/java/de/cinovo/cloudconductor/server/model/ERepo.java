package de.cinovo.cloudconductor.server.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Repo;
import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "repo", schema = "cloudconductor")
public class ERepo extends AModelApiConvertable<Repo> implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	
	private String name;
	private String description;
	private List<ERepoMirror> repoMirrors;
	private Long primaryMirrorId;
	
	private Long lastIndex;
	
	
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
	 * @return the name
	 */
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
	 * @return the repoMirrors
	 */
	@OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER, mappedBy = "repo")
	public List<ERepoMirror> getRepoMirrors() {
		return this.repoMirrors;
	}
	
	/**
	 * @param repoMirrors the repoMirrors to set
	 */
	public void setRepoMirrors(List<ERepoMirror> repoMirrors) {
		this.repoMirrors = repoMirrors;
	}
	
	/**
	 * @return the id of the primary mirror
	 */
	public Long getPrimaryMirrorId() {
		return this.primaryMirrorId;
	}
	
	/**
	 * 
	 * @param primaryMirrorId the id of the primary mirror to be set
	 */
	public void setPrimaryMirrorId(Long primaryMirrorId) {
		this.primaryMirrorId = primaryMirrorId;
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
	
	@Override
	@Transient
	public Class<Repo> getApiClass() {
		return Repo.class;
	}
	
	@Override
	public Repo toApi() {
		Repo repo = super.toApi();
		repo.setMirrors(this.convertableModelToApiSet(this.repoMirrors));
		repo.setPrimaryMirror(this.primaryMirrorId);
		return repo;
	}
}
