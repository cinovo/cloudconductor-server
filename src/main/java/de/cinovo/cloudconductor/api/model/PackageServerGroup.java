package de.cinovo.cloudconductor.api.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageServerGroup implements INamed {
	
	private String name;
	private Set<Long> packageServers;
	private Long primaryServer;
	
	
	/**
	 * @param name the name
	 * @param packageServers the package server ids
	 * @param primaryServer the primary server id
	 */
	public PackageServerGroup(@JsonProperty("name") String name, @JsonProperty("packageServers") Set<Long> packageServers, @JsonProperty("primaryServer") Long primaryServer) {
		super();
		this.name = name;
		this.packageServers = packageServers;
		this.primaryServer = primaryServer;
	}
	
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
	public Set<Long> getPackageServers() {
		return this.packageServers;
	}
	
	/**
	 * @param packageServers the packageServers to set
	 */
	public void setPackageServers(Set<Long> packageServers) {
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
	
}
