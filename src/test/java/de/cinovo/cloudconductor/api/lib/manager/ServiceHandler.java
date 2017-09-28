package de.cinovo.cloudconductor.api.lib.manager;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.util.Set;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.Service;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ServiceHandler extends DefaultRestHandler<Service> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public ServiceHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	/**
	 * @param cloudconductorUrl the config server url
	 * @param token the token
	 * @param agent the agent
	 */
	public ServiceHandler(String cloudconductorUrl, String token, String agent) {
		super(cloudconductorUrl);
		this.setTokenMode(token, agent);
	}
	
	@Override
	protected String getDefaultPath() {
		return IRestPath.SERVICE;
	}
	
	@Override
	protected Class<Service> getAPIClass() {
		return Service.class;
	}
	
	/**
	 * @param service the service name
	 * @return packages associated with the service
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<Package> getPackages(String service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.SERVICE_PKG, service);
		return (Set<Package>) this._get(path, this.getSetType(Package.class));
	}
	
	/**
	 * @param service the service name
	 * @param pkg the package name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addPackage(String service, String pkg) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.SERVICE_PKG_SINGLE, service, pkg);
		this._put(path);
	}
	
	/**
	 * @param service the service name
	 * @param pkg the package name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removePackage(String service, String pkg) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.SERVICE_PKG_SINGLE, service, pkg);
		this._delete(path);
	}
	
	/**
	 * @param service the service name
	 * @param host the host name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void approveStarted(String service, String host) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.SERVICE_APPROVE_STARTED, service, host);
		this._put(path);
	}
}
