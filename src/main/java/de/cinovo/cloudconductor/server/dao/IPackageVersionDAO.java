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
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IPackageVersionDAO extends IEntityDAO<EPackageVersion, Long> {
	
	/**
	 * @param baseName the package base name
	 * @param version the version
	 * @return single package version
	 */
	EPackageVersion find(String baseName, String version);
	
	/**
	 * @param baseName the base name
	 * @return list of package versions
	 */
	List<EPackageVersion> find(String baseName);

	/**
	 * @param templateId	id of the template
	 * @return list of package versions in template
	 */
	List<EPackageVersion> findByTemplate(Long templateId);

	/**
	 * @param templateName	the name of the template
	 * @return list of package versions for template
	 */
	List<EPackageVersion> findByTemplate(String templateName);

	/**
	 * @param repoName	the name of the repo
	 * @return list of package versions provided by repo
	 */
	List<EPackageVersion> findByRepo(String repoName);

}
