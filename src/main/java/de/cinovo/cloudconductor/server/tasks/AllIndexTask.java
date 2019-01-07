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

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public class AllIndexTask implements IServerTasks {

	/**
	 * prefix for id of repository index tasks
	 */
	private static final ReentrantLock WORKING = new ReentrantLock();

	private IRepoDAO repoDAO;
	private RepoHandler repoHandler;
	private IPackageImport packageImport;
	private Integer timer;
	private TimeUnit timerUnit;
	private Integer delay;


	/**
	 * @param repoDAO       the repo dao
	 * @param repoHandler   the repo handler
	 * @param packageImport the package import
	 * @param timer         the timer
	 * @param timerUnit     the timer unit
	 * @param delay         the delay in timerUnit
	 */
	public AllIndexTask(IRepoDAO repoDAO, RepoHandler repoHandler, IPackageImport packageImport, Integer timer, TimeUnit timerUnit, Integer delay) {
		this.repoDAO = repoDAO;
		this.repoHandler = repoHandler;
		this.packageImport = packageImport;
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
		return "AllIndexTask";
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

	@Override
	public void run() {
		if(AllIndexTask.WORKING.tryLock()) {
			try {
				List<ERepo> list = this.repoDAO.findList();
				if((list == null) || list.isEmpty()) {
					return;
				}
				for(ERepo repo : list) {
					SingleIndexTask singleIndexTask = new SingleIndexTask(this.repoDAO, this.repoHandler, this.packageImport, repo.getId(), this.timer, this.timerUnit, this.delay);
					singleIndexTask.run();
				}
			} finally {
				AllIndexTask.WORKING.unlock();
			}
		}
	}

}
