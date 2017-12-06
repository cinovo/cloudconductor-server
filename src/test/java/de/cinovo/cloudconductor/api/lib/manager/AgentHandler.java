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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.AbstractApiHandler;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.cinovo.cloudconductor.api.model.Template;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class AgentHandler extends AbstractApiHandler {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public AgentHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	/**
	 * @param cloudconductorUrl the config server url
	 * @param token the token
	 * @param agent the agent
	 */
	public AgentHandler(String cloudconductorUrl, String token, String agent) {
		super(cloudconductorUrl);
		this.setTokenMode(token, agent);
	}
	
	/**
	 * @param template the template name
	 * @param host the host name
	 * @param state the package state
	 * @param uuid the UUID
	 * @return changes to the package state
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public PackageStateChanges notifyPackageState(String template, String host, PackageState state, String uuid) throws CloudConductorException {
		String path = this.pathGenerator("/agent/{template}/{host}/{uuid}/package", template, host, uuid);
		return this._put(path, state, PackageStateChanges.class);
	}
	
	/**
	 * @param template the template name
	 * @param host the host name
	 * @param state the package state
	 * @param uuid the UUID
	 * @return changes to the service state
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public ServiceStatesChanges notifyServiceState(String template, String host, ServiceStates state, String uuid) throws CloudConductorException {
		String path = this.pathGenerator("/agent/{template}/{host}/{uuid}/service", template, host, uuid);
		return this._put(path, state, ServiceStatesChanges.class);
	}
	
	/**
	 * @param configFilename the name of the config file
	 * @return the data of the config file
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public String getConfigFileData(String configFilename) throws CloudConductorException {
		String path = this.pathGenerator("/file/{name}/data", configFilename);
		return this._get(path, String.class);
	}
	
	/**
	 * @param template the template name
	 * @return the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Template getTemplate(String template) throws CloudConductorException {
		String path = this.pathGenerator("/template/{name}", template);
		return this._get(path, Template.class);
	}
	
	/**
	 * @param template the template name
	 * @return the services of the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<Service> getServices(String template) throws CloudConductorException {
		String path = this.pathGenerator("/template/{template}/services", template);
		return (Set<Service>) this._get(path, this.getSetType(Service.class));
	}
	
	/**
	 * @param template the template name
	 * @return the ssh keys of the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<SSHKey> getSSHKeys(String template) throws CloudConductorException {
		String path = this.pathGenerator("/template/{template}/sshkeys", template);
		return (Set<SSHKey>) this._get(path, this.getSetType(SSHKey.class));
	}
	
	/**
	 * @return collection of alive host names
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getAliveAgents() throws CloudConductorException {
		String path = this.pathGenerator("/agent");
		return (Set<String>) this._get(path, this.getSetType(String.class));
	}
	
	/**
	 * @param template the template name
	 * @param host the host name
	 * @param agent the agent
	 * @param uuid the UUID
	 * @return the agent options of the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public AgentOption heartBeat(String template, String host, String agent, String uuid) throws CloudConductorException {
		String path = this.pathGenerator("/agent/{template}/{host}/{agent}/{uuid}/heartbeat", template, host, agent, uuid);
		return this._get(path, AgentOption.class);
	}
}
