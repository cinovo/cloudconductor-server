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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
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

	private static final Logger logger = LoggerFactory.getLogger(IndexTask.class);
	
	@Autowired
	private IRepoProvider repo;

	@Autowired
	private IRepoIndexer indexer;

	@Autowired
	private IPackageImport packageImport;

	@Autowired
	private ObjectMapper mapper = MapperFactory.createDefault();
	
	
	@Override
	public void run() {
		try {
			Set<PackageVersion> latestIndex = this.indexer.getRepoIndex(this.repo);
			if (latestIndex != null) {
				this.packageImport.importVersions(latestIndex);
			}
		} catch (Exception e) {
			IndexTask.logger.error("Error indexing repo", e);
		}
	}
}
