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

import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.EFileTag;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IFileDAO extends IEntityDAO<EFile, Long>, IFindNamed<EFile> {
	
	/**
	 * @param configFileId the configFileId
	 * @return the config data
	 */
	public EFileData findDataByFile(long configFileId);
	
	/**
	 * @return the row count
	 */
	public Long count();
	
	public List<EFile> findByTag(String... tagnames);
	
	public List<EFile> findByTag(EFileTag... tags);
	
}
