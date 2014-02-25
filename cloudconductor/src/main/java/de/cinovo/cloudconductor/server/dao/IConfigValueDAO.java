package de.cinovo.cloudconductor.server.dao;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.util.List;

import de.cinovo.cloudconductor.server.model.config.EConfigValue;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IConfigValueDAO extends IEntityDAO<EConfigValue, Long> {
	
	/**
	 * @return the global configuration
	 */
	public List<EConfigValue> findGlobal();
	
	/**
	 * @param service the service name
	 * @return the global configuration for the given service
	 */
	public List<EConfigValue> findGlobal(String service);
	
	/**
	 * @param service the service name
	 * @param key the key
	 * @return the value of the global key within the configuration of the given service
	 */
	public EConfigValue findGlobal(String service, String key);
	
	/**
	 * @param template the template name
	 * @return the configuration for the given template
	 */
	public List<EConfigValue> findBy(String template);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @return the configuration for the service within the given template
	 */
	public List<EConfigValue> findBy(String template, String service);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @param key the key
	 * @return the value of the key within the configuration for the service within the given template
	 */
	public EConfigValue findBy(String template, String service, String key);
	
	/**
	 * @param key the key
	 * @return the global value of the key
	 */
	public EConfigValue findKey(String key);
	
	/**
	 * @param template the template name
	 * @param key the key
	 * @return the value of the key for the given template
	 */
	public EConfigValue findKey(String template, String key);
	
	/**
	 * @param template the template name
	 * @return foll list of config values for the given template
	 */
	public List<EConfigValue> findAll(String template);
}
