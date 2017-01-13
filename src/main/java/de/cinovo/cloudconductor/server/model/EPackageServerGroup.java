package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.*;
import java.util.List;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "packageservergroup", schema = "cloudconductor")
public class EPackageServerGroup extends AModelApiConvertable<PackageServerGroup> implements IEntity<Long> {

	private static final long serialVersionUID = 1L;
	private Long id;

	private String name;
	private List<EPackageServer> packageServers;
	private Long primaryServerId;


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
	 * @return the packageServers
	 */
	@OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER, mappedBy = "serverGroup")
	public List<EPackageServer> getPackageServers() {
		return this.packageServers;
	}

	/**
	 * @param packageServers the packageServers to set
	 */
	public void setPackageServers(List<EPackageServer> packageServers) {
		this.packageServers = packageServers;
	}

	public Long getPrimaryServerId() {
		return this.primaryServerId;
	}

	public void setPrimaryServerId(Long primaryServerId) {
		this.primaryServerId = primaryServerId;
	}


	@Override
	@Transient
	public Class<PackageServerGroup> getApiClass() {
		return PackageServerGroup.class;
	}


	public PackageServerGroup toApi() {
		PackageServerGroup psg = super.toApi();
		psg.setPackageServers(this.convertableModelToApiSet(this.packageServers));
		psg.setPrimaryServer(this.primaryServerId);
		return psg;
	}
}
