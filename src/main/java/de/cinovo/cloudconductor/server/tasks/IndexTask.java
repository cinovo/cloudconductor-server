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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public class IndexTask implements IServerTasks {
	
	/**
	 * prefix for id of repository index tasks
	 */
	public static final String TASK_ID_PREFIX = "REPO_INDEX_TASK_REPOID_";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IRepoDAO repoDAO;
	private RepoHandler repoHandler;
	private IPackageImport packageImport;
	
	private Long repoId;
	
	private Integer timer;
	private TimeUnit timerUnit;
	private Integer delay;
	
	
	/**
	 * @param repoDAO the repo dao
	 * @param repoHandler the repo handler
	 * @param packageImport the package import
	 * @param repoId the repo id
	 * @param timer the timer
	 * @param timerUnit the timer unit
	 * @param delay the delay in timerUnit
	 */
	public IndexTask(IRepoDAO repoDAO, RepoHandler repoHandler, IPackageImport packageImport, Long repoId, Integer timer, TimeUnit timerUnit, Integer delay) {
		this.repoDAO = repoDAO;
		this.repoHandler = repoHandler;
		this.packageImport = packageImport;
		this.repoId = repoId;
		this.timer = timer;
		this.timerUnit = timerUnit;
		this.delay = delay;
	}
	
	@Override
	public Integer getDelay() {
		return this.delay;
	}
	
	@Override
	public String getTaskIdentifier() {
		return IndexTask.TASK_ID_PREFIX + this.repoId;
	}
	
	@Override
	public Integer getTimer() {
		return this.timer;
	}
	
	@Override
	public TimeUnit getTimerUnit() {
		return this.timerUnit;
	}
	
	@Override
	public TaskStateChange checkStateChange(EServerOptions oldSettings, EServerOptions newSettings) {
		return TaskStateChange.START;
	}
	
	/**
	 * forces a re index, ignoring checksum
	 */
	public void forceRun() {
		this.execute(true);
	}
	
	@Override
	public void run() {
		this.execute(false);
	}
	
	private void execute(boolean force) {
		if (this.repoId == null) {
			return;
		}
		ERepo repo = this.repoDAO.findById(this.repoId);
		if (repo == null) {
			this.logger.error("Failed to find the repo with id {}", this.repoId);
			return;
		}
		ERepoMirror mirror = this.repoHandler.findPrimaryMirror(repo);
		if (mirror == null) {
			this.logger.error("Failed to find a mirror for the repo {}", repo.getName());
			return;
		}
		
		this.logger.debug("Start task '{}' indexing mirror '{}' of Repository '{}'", this.getTaskIdentifier(), mirror.getPath(), repo.getName());
		String checksum = this.indexRepo(mirror, repo.getLastIndexHash(), force);
		if (checksum != null) {
			repo.setLastIndex(DateTime.now().getMillis());
			repo.setLastIndexHash(checksum);
			this.repoDAO.save(repo);
		}
		this.logger.debug("End of Index Task '{}'.", this.getTaskIdentifier());
	}
	
	private String indexRepo(ERepoMirror mirror, String oldChecksum, boolean force) {
		try {
			IRepoProvider repoProvider = this.repoHandler.findRepoProvider(mirror);
			if (repoProvider == null) {
				throw new CloudConductorException("No repo provider for mirror '" + mirror.getPath() + "'!");
			}
			IRepoIndexer indexer = this.repoHandler.findRepoIndexer(mirror);
			if (indexer == null) {
				throw new CloudConductorException("No indexer for mirror '" + mirror.getPath() + "'!");
			}
			RepoEntry entry = indexer.getRepoEntry(repoProvider);
			if (entry == null) {
				throw new CloudConductorException("No repo entry to index for mirror '" + mirror.getPath() + "' found!");
			}
			if ((oldChecksum != null) && oldChecksum.equals(entry.getChecksum()) && !force) {
				this.logger.debug("Skipped repo indexing, no new files for mirror '{}'", mirror.getPath());
				return entry.getChecksum();
			}
			Set<PackageVersion> latestIndex = indexer.getRepoIndex(repoProvider);
			if (latestIndex != null) {
				this.logger.debug("Latest index includes " + latestIndex.size() + " package versions!");
				this.packageImport.importVersions(latestIndex);
				return entry.getChecksum();
			}
		} catch (Exception e) {
			this.logger.error("Error indexing repo '" + mirror.getPath() + "'", e);
		}
		return null;
	}
	
}
