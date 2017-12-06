package de.cinovo.cloudconductor.api.lib.manager;

import java.util.Set;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.AbstractApiHandler;
import de.cinovo.cloudconductor.api.model.PackageVersion;

/**
 * Copyright 2014<br>
 * <br>
 * 
 * @author Fabian Toth
 * 
 */
public class IOModuleHandler extends AbstractApiHandler {
	
	/**
	 * Constructor
	 * 
	 * @param cloudconductorUrl the url of the Cloud Conductor Server
	 */
	public IOModuleHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	/**
	 * @param cloudconductorUrl the config server url
	 * @param token the token
	 * @param agent the agent
	 */
	public IOModuleHandler(String cloudconductorUrl, String token, String agent) {
		super(cloudconductorUrl);
		this.setTokenMode(token, agent);
	}
	
	/**
	 * @param versions the versions of a package to add
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void importPackages(Set<PackageVersion> versions) throws CloudConductorException {
		String path = this.pathGenerator("/io/versions");
		this._post(path, versions, this.getSetType(PackageVersion.class));
	}
}
