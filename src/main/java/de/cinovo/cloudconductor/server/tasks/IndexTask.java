package de.cinovo.cloudconductor.server.tasks;

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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public class IndexTask implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private IRepoDAO repoDAO;
	private RepoHandler repoHandler;
	private IPackageImport packageImport;

	private Long repoId;

	/**
	 * @param repoDAO       the repo dao
	 * @param repoHandler   the repo handler
	 * @param packageImport the package import
	 * @param repoId        the repo id
	 */
	public IndexTask(IRepoDAO repoDAO, RepoHandler repoHandler, IPackageImport packageImport, Long repoId) {
		this.repoDAO = repoDAO;
		this.repoHandler = repoHandler;
		this.packageImport = packageImport;
		this.repoId = repoId;
	}

	@Override
	public void run() {
		if(this.repoId == null) {
			return;
		}
		this.logger.debug("Start Index Task!");
		ERepo repo = this.repoDAO.findById(this.repoId);
		if(repo == null) {
			this.logger.error("Failed to find the repo with id {}", repoId);
			return;
		}
		ERepoMirror mirror = this.repoHandler.findPrimaryMirror(repo);
		if(mirror == null) {
			this.logger.error("Failed to find a mirror for the repo {}", repo.getName());
		}

		this.logger.debug("Start indexing mirror '" + mirror.getPath() + "' of Repository '" + repo.getName() + "'");
		String checksum = this.indexRepo(mirror, repo.getLastIndexHash());
		if(checksum != null) {
			repo.setLastIndex(DateTime.now().getMillis());
			repo.setLastIndexHash(checksum);
			this.repoDAO.save(repo);
		}
		this.logger.debug("End of Index Task.");
	}

	private String indexRepo(ERepoMirror mirror, String oldChecksum) {
		try {
			IRepoProvider repoProvider = this.repoHandler.findRepoProvider(mirror);
			if(repoProvider == null) {
				throw new CloudConductorException("No repo provider for mirror '" + mirror.getPath() + "'!");
			}
			IRepoIndexer indexer = this.repoHandler.findRepoIndexer(mirror);
			if(indexer == null) {
				throw new CloudConductorException("No indexer for mirror '" + mirror.getPath() + "'!");
			}
			RepoEntry entry = indexer.getRepoEntry(repoProvider);
			if(entry == null) {
				throw new CloudConductorException("No repo entry to index for mirror '" + mirror.getPath() + "' found!");
			}
			if(oldChecksum != null && oldChecksum.equals(entry.getChecksum())) {
				this.logger.debug("Skipped repo indexing, no new files for mirror {}", mirror.getPath());
				return entry.getChecksum();
			}
			Set<PackageVersion> latestIndex = indexer.getRepoIndex(repoProvider);
			if(latestIndex != null) {
				this.logger.debug("Latest index includes " + latestIndex.size() + " package versions!");
				this.packageImport.importVersions(latestIndex);
				return entry.getChecksum();
			}
		} catch(Exception e) {
			this.logger.error("Error indexing repo '" + mirror.getPath() + "'", e);
		}
		return null;
	}

}
