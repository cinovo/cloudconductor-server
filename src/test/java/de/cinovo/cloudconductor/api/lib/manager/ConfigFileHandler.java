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
import de.cinovo.cloudconductor.api.model.ConfigFile;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigFileHandler extends DefaultRestHandler<ConfigFile> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public ConfigFileHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	/**
	 * @param cloudconductorUrl the config server url
	 * @param token the token
	 * @param agent the agent
	 */
	public ConfigFileHandler(String cloudconductorUrl, String token, String agent) {
		super(cloudconductorUrl);
		this.setTokenMode(token, agent);
	}
	
	@Override
	protected String getDefaultPath() {
		return IRestPath.FILE;
	}
	
	@Override
	protected Class<ConfigFile> getAPIClass() {
		return ConfigFile.class;
	}
	
	/**
	 * @param name the config name
	 * @return the data of the config file
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public String getData(String name) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.FILE_DATA, name);
		return this._get(path, String.class);
	}
	
	/**
	 * @param name the config name
	 * @param data the data of the config file
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void saveData(String name, String data) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.FILE_DATA, name);
		this._put(path, data);
	}
	
	/**
	 * @param template the template name
	 * @return the config files of the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<ConfigFile> getConfigFilesByTemplate(String template) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.FILE_TEMPLATE, template);
		return (Set<ConfigFile>) this._get(path, this.getSetType(ConfigFile.class));
	}
}
