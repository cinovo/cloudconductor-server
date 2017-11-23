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

import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IServiceDefaultStateDAO extends IEntityDAO<EServiceDefaultState, Long> {
	
	/**
	 * @param name the service name
	 * @param template the template name
	 * @return the default service state
	 */
	EServiceDefaultState findByName(String name, String template);
	
	/**
	 * @param template the template name
	 * @return list off default services
	 */
	List<EServiceDefaultState> findByTemplate(String template);
}
