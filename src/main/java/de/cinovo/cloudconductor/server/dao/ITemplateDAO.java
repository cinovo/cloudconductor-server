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

import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */

public interface ITemplateDAO extends IEntityDAO<ETemplate, Long>, IFindNamed<ETemplate> {
	
	/**
	 * Disable auto update for all templates
	 *
	 * @return number of updated templates
	 */
	int disableAutoUpdate();
	
	/**
	 * @param templateNames template names
	 * @return list of templates
	 */
	List<ETemplate> findByName(Iterable<String> templateNames);
	
	/**
	 * @return the number of templates
	 */
	Long count();
	
	/**
	 * @param repo the repository
	 * @return number of templates using given repository
	 */
	Long countUsingRepo(ERepo repo);
	
	/**
	 * @param repo           the repository
	 * @param packageVersion the package version
	 * @return number of templates using repo and package version
	 */
	Long countUsingPackageVersion(ERepo repo, EPackageVersion packageVersion);
	
	/**
	 * @param templates the templates ids
	 * @return the templates
	 */
	List<ETemplate> findByIds(Iterable<Long> templates);
	
	/**
	 * @param templateId the template id
	 * @return the name of the template
	 */
	String findNameById(Long templateId);
}
