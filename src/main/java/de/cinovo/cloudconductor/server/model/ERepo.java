package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "repo", schema = "cloudconductor")
public class ERepo implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String description;
	private Long primaryMirrorId;
	private Long lastIndex;
	private String lastIndexHash;
	
	
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
	 * @return the id of the primary mirror
	 */
	public Long getPrimaryMirrorId() {
		return this.primaryMirrorId;
	}
	
	/**
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
	
	/**
	 * @return the lastIndexHash
	 */
	public String getLastIndexHash() {
		return this.lastIndexHash;
	}
	
	/**
	 * @param lastIndexHash the lastIndexHash to set
	 */
	public void setLastIndexHash(String lastIndexHash) {
		this.lastIndexHash = lastIndexHash;
	}
	
	@Override
	@Transient
	public boolean equals(Object obj) {
		if (!(obj instanceof ERepo)) {
			return false;
		}
		
		ERepo other = (ERepo) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
	}
	
	@Override
	@Transient
	public int hashCode() {
		return Objects.hash(this.id, this.name);
	}
	
	/**
	 * @param mirrorDAO the mirror dao
	 * @return the api object
	 */
	@Transient
	public Repo toApi(IRepoMirrorDAO mirrorDAO) {
		Repo repo = GenericModelApiConverter.convert(this, Repo.class);
		repo.setMirrors(mirrorDAO.findForRepo(this).stream().map(ERepoMirror::toApi).collect(Collectors.toSet()));
		repo.setPrimaryMirror(this.primaryMirrorId);
		return repo;
	}
}
