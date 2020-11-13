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

import de.cinovo.cloudconductor.server.model.EHost;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IHostDAO extends IEntityDAO<EHost, Long>, IFindNamed<EHost> {
	
	/**
	 * @return the number of hosts
	 */
	Long count();
	
	/**
	 * @param uuid the uuid
	 * @return the host or null
	 */
	EHost findByUuid(String uuid);
	
	/**
	 * @param templateId the id of the template
	 * @return list of hosts with given template
	 */
	List<EHost> findHostsForTemplate(Long templateId);
	
	/**
	 * @param templateId the template id
	 * @return the number of hosts of a template
	 */
	Long countForTemplate(Long templateId);
}
