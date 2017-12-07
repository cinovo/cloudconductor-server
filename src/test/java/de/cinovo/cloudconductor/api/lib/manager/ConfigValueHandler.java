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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.AbstractApiHandler;
import de.cinovo.cloudconductor.api.model.ConfigValue;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class ConfigValueHandler extends AbstractApiHandler {

	/**
	 * @param cloudconductorUrl the config server url
	 */
	public ConfigValueHandler(String cloudconductorUrl, String token) {
		super(cloudconductorUrl, token);
	}


	/**
	 * @param template the template name
	 * @return map containing config kv-pairs of the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<ConfigValue> getConfig(String template) throws CloudConductorException {
		String path = this.pathGenerator("/config/{template}", template);
		return (Set<ConfigValue>) this._get(path, this.getSetType(ConfigValue.class));
	}

	/**
	 * @param template the template name
	 * @param service  the serice name
	 * @return map containing config kv-pairs of the service
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<ConfigValue> getConfig(String template, String service) throws CloudConductorException {
		String path = this.pathGenerator("/config/{template}/{service}", template, service);
		return (Set<ConfigValue>) this._get(path, this.getSetType(ConfigValue.class));
	}

	/**
	 * @param template the template name
	 * @param service  the serice name
	 * @param key      the config key
	 * @return the value
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public String getConfig(String template, String service, String key) throws CloudConductorException {
		String path = this.pathGenerator("/config/{template}/{service:.*}/{key}", template, service, key);
		return this._get(path, String.class);
	}

	/**
	 * @param template the template name
	 * @param key      the config key
	 * @param value    the config value
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addConfig(String template, String key, String value) throws CloudConductorException {
		String path = this.pathGenerator("/config");
		ConfigValue cv = new ConfigValue();
		cv.setKey(key);
		cv.setTemplate(template);
		cv.setValue(value);
		this._put(path, cv);
	}

	/**
	 * @param template the template name
	 * @param service  the service name
	 * @param key      the config key
	 * @param value    the config value
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addConfig(String template, String service, String key, String value) throws CloudConductorException {
		String path = this.pathGenerator("/config");
		ConfigValue cv = new ConfigValue();
		cv.setKey(key);
		cv.setTemplate(template);
		cv.setValue(value);
		cv.setService(service);
		this._put(path, cv);
	}

	/**
	 * @param template the template name
	 * @param service  the service name
	 * @param key      the config key
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removeConfig(String template, String service, String key) throws CloudConductorException {
		String path = this.pathGenerator("/config/{template}/{service:.*}/{key}", template, service, key);
		this._delete(path);
	}

}
