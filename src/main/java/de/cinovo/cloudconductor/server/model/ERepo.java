package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Repo;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;

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
	public Class<Repo> getApiClass() {
		return Repo.class;
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		if(!(obj instanceof ERepo)) {
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

	@Override
	public Repo toApi() {
		Repo repo = super.toApi();
		repo.setMirrors(this.convertableModelToApiSet(this.repoMirrors));
		repo.setPrimaryMirror(this.primaryMirrorId);
		return repo;
	}
}
