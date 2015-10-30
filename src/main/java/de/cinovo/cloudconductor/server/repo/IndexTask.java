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

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.IndexFileIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.RPMIndexer;
import de.cinovo.cloudconductor.server.repo.provider.AWSS3Provider;
import de.cinovo.cloudconductor.server.repo.provider.FileProvider;
import de.cinovo.cloudconductor.server.repo.provider.HTTPProvider;
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
	private IPackageImport packageImport;
	
	// FIXME initialize OR autowire but not both
	@Autowired
	private ObjectMapper mapper = MapperFactory.createDefault();
	
	@Autowired
	private IPackageServerDAO psdao;
	
	
	@Override
	public void run() {
		List<EPackageServer> packageservers = this.psdao.findOnePerGroup();
		for (EPackageServer server : packageservers) {
			try {
				IRepoProvider repo = this.findProvider(server);
				if (repo == null) {
					IndexTask.logger.error("Error indexing repo " + server.getPath() + ", repo provider was not set");
					continue;
				}
				
				IRepoIndexer indexer = this.findIndexer(server);
				
				if (indexer == null) {
					IndexTask.logger.error("Error indexing repo " + server.getPath() + ", indexer was not set");
					continue;
				}
				
				Set<PackageVersion> latestIndex = indexer.getRepoIndex(repo);
				if (latestIndex != null) {
					this.packageImport.importVersions(latestIndex);
				}
			} catch (Exception e) {
				IndexTask.logger.error("Error indexing repo " + server.getPath(), e);
			}
		}
	}
	
	private IRepoIndexer findIndexer(EPackageServer server) {
		switch (server.getIndexerType()) {
		case FILE:
			return new IndexFileIndexer();
		case RPM:
			return new RPMIndexer();
		default:
			break;
		}
		return null;
	}
	
	private IRepoProvider findProvider(EPackageServer server) {
		switch (server.getProviderType()) {
		case AWSS3:
			return new AWSS3Provider(server);
		case FILE:
			return new FileProvider(server);
		case HTTP:
			return new HTTPProvider(server);
		default:
			break;
		}
		return null;
	}
}
