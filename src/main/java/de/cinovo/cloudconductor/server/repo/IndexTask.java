package de.cinovo.cloudconductor.server.repo;

/*
 * #%L cloudconductor-server %% Copyright (C) 2013 - 2014 Cinovo AG %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;

import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.cinovo.cloudconductor.server.util.IPackageImport;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
@Service("indextask")
public class IndexTask implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexTask.class);

	@Autowired
	private IRepoProvider repo;
	
	@Autowired
	private IPackageImport packageImport;
	
	@Autowired
	private ObjectMapper mapper = MapperFactory.createDefault();


	@Override
	public void run() {
		String latestIndex = this.repo.getLatestIndex();
		if (latestIndex != null) {
			try {
				CollectionType indexType = CollectionType.construct(Set.class, SimpleType.construct(PackageVersion.class));
				Set<PackageVersion> set = this.mapper.readValue(latestIndex, indexType);
				this.packageImport.importVersions(set);
			} catch (IOException e) {
				IndexTask.LOGGER.error("Failed to import package index", e);
			}
		}
	}
}
