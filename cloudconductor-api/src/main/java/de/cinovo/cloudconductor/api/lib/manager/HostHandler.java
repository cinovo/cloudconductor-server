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
import de.cinovo.cloudconductor.api.lib.exceptions.ClientErrorException;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.Service;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class HostHandler extends DefaultRestHandler<Host> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public HostHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	@Override
	protected String getDefaultPath() {
		return IRestPath.HOST;
	}
	
	@Override
	protected Class<Host> getAPIClass() {
		return Host.class;
	}
	
	/**
	 * @param name the host name
	 * @return services of the host
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Set<Service> getServices(String name) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICES, name);
		return (Set<Service>) this._get(path, this.getSetType(Service.class));
	}
	
	/**
	 * @param name the host name
	 * @param service the service
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void setService(String name, Service service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICE_SVC, name, service.getName());
		this._put(path, service);
	}
	
	/**
	 * @param name the host name
	 * @param service the service name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removeService(String name, String service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICE_SVC, name, service);
		this._delete(path);
	}
	
	/**
	 * @param host the host name
	 * @return whether host packages are in sync with template or not
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Boolean inSync(String host) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SYNC, host);
		return this._get(path, Boolean.class);
	}
	
	/**
	 * @param host the host name
	 * @param service the service name
	 * @return whether the service was set to be started or not
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Boolean startService(String host, String service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICE_START, host, service);
		try {
			this._put(path);
			return true;
		} catch (ClientErrorException e) {
			return false;
		}
	}
	
	/**
	 * @param host the host name
	 * @param service the service name
	 * @return whether the service was set to be stopped or not
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Boolean stopService(String host, String service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICE_STOP, host, service);
		try {
			this._put(path);
			return true;
		} catch (ClientErrorException e) {
			return false;
		}
	}
	
	/**
	 * @param host the host name
	 * @param service the service name
	 * @return whether the service was set to be restarted or not
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Boolean restartService(String host, String service) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.HOST_SERVICE_RESTART, host, service);
		try {
			this._put(path);
			return true;
		} catch (ClientErrorException e) {
			return false;
		}
	}
}
