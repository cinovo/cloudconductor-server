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

import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IConfigValueDAO extends IEntityDAO<EConfigValue, Long> {
	
	/**
	 * @param template the template name
	 * @return the configuration for the given template
	 */
	List<EConfigValue> findBy(String template);
	
	/**
	 * @return configuration values for global template
	 */
	List<EConfigValue> findForGlobalTemplate();

	/**
	 * @param templateName the name of the template
	 * @return list of configuration values for GLOBAL service of template
	 */
	List<EConfigValue> findForGlobalService(String templateName);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @return the configuration for the service within the given template
	 */
	List<EConfigValue> findBy(String template, String service);
	
	/**
	 * @param template the template name
	 * @param service the service name
	 * @param key the key
	 * @return the value of the key within the configuration for the service within the given template
	 */
	EConfigValue findBy(String template, String service, String key);
	
	/**
	 * @param template the template name
	 * @return set of service names for given template
	 */
	Set<String> findServicesForTemplate(String template);
	
	/**
	 * @return set of available templates
	 */
	Set<String> findRealTemplates();
	
	/**
	 * @return list of templates available in config
	 */
	List<String> findTemplates();
	
	/**
	 * @param template the name of the template
	 * @return list of configuration
	 */
	List<EConfigValue> findAll(String template);

	// TODO int deleteByTemplate(String templateName)
	// TODO int deleteByTemplateService(String template, String service)
	// TODO int deleteKV(String template, String service, String key)

}
