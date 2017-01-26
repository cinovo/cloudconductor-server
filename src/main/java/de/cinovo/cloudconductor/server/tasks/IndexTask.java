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

import de.cinovo.cloudconductor.api.lib.helper.SchedulerService;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
@Service()
public class IndexTask implements IServerTasks {

	private static final Logger logger = LoggerFactory.getLogger(IndexTask.class);

	@Autowired
	private IPackageImport packageImport;

	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private RepoHandler repoHandler;

	@Override
	public void run() {
		List<ERepo> repos = this.repoDAO.findList();
		for(ERepo repo : repos) {
			ERepoMirror mirror = this.repoHandler.findPrimaryMirror(repo);
			if(this.indexRepo(mirror)) {
				repo.setLastIndex(DateTime.now().getMillis());
				this.repoDAO.save(repo);
			}
		}
	}

	private boolean indexRepo(ERepoMirror mirror) {
		try {
			IRepoProvider repoProvider = this.repoHandler.findRepoProvider(mirror);
			if(repoProvider == null) {
				return false;
			}
			IRepoIndexer indexer = this.repoHandler.findRepoIndexer(mirror);
			if(indexer == null) {
				return false;
			}

			Set<PackageVersion> latestIndex = indexer.getRepoIndex(repoProvider);
			if(latestIndex != null) {
				this.packageImport.importVersions(latestIndex);
				return true;
			}
		} catch(Exception e) {
			IndexTask.logger.error("Error indexing repo " + mirror.getPath(), e);
		}
		return false;
	}

	@Override
	public String getTaskIdentifier() {
		return "INDEXER_TASK";
	}

	@Override
	public void create(EServerOptions settings) {
		SchedulerService.instance.register(this.getTaskIdentifier(), this, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit());
	}

	@Override
	public void update(EServerOptions oldSettings, EServerOptions newSettings) {
		boolean change = oldSettings == null;
		if(!change) {
			if(oldSettings.getIndexScanTimer() != newSettings.getIndexScanTimer()) {
				change = true;
			}
			if(!oldSettings.getIndexScanTimerUnit().equals(newSettings.getIndexScanTimerUnit())) {
				change = true;
			}
		}
		if(change) {
			SchedulerService.instance.resetTask(this.getTaskIdentifier(), newSettings.getIndexScanTimer(), newSettings.getIndexScanTimerUnit());
		}
	}

}
