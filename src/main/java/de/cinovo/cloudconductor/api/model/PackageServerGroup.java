package de.cinovo.cloudconductor.api.model;

import java.util.Set;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageServerGroup implements INamed {
	
	private Long id;
	private String name;
	private Set<PackageServer> packageServers;
	private Long primaryServer;
	
	

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the packageServers
	 */
	public Set<PackageServer> getPackageServers() {
		return this.packageServers;
	}
	
	/**
	 * @param packageServers the packageServers to set
	 */
	public void setPackageServers(Set<PackageServer> packageServers) {
		this.packageServers = packageServers;
	}
	
	/**
	 * @return the primaryServer
	 */
	public Long getPrimaryServer() {
		return this.primaryServer;
	}
	
	/**
	 * @param primaryServer the primaryServer to set
	 */
	public void setPrimaryServer(Long primaryServer) {
		this.primaryServer = primaryServer;
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
	
}
