package de.cinovo.cloudconductor.server.tasks;

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

import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service()
public class CleanUpTask implements IServerTasks {

	private static final Logger LOGGER = LoggerFactory.getLogger(CleanUpTask.class);

	private static final int MAX_AGE = 180;

	@Autowired
	private IHostDAO hostDAO;
	private TimeUnit hostCleanUpTimerUnit = null;
	private Integer hostCleanUpTimer = null;


	@Override
	public void run() {
		List<EHost> hosts = this.hostDAO.findList();
		for(EHost host : hosts) {
			DateTime now = new DateTime();
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			if(diff > CleanUpTask.MAX_AGE) {
				this.hostDAO.delete(host);
				CleanUpTask.LOGGER.info("Deleted host '" + host.getName() + "' during clean up.");
			}
		}
	}

	@Override
	public String getTaskIdentifier() {
		return "CLEAN_UP_TASK";
	}

	@Override
	public Integer getTimer() {
		return this.hostCleanUpTimer;
	}

	@Override
	public TimeUnit getTimerUnit() {
		return this.hostCleanUpTimerUnit;
	}

	@Override
	public Integer getDelay() {
		return 0;
	}

	@Override
	public TaskStateChange checkStateChange(EServerOptions oldSettings, EServerOptions newSettings) {
		boolean change = false;
		if(oldSettings == null) {
			this.hostCleanUpTimer = newSettings.getHostCleanUpTimer();
			this.hostCleanUpTimerUnit = newSettings.getHostCleanUpTimerUnit();
			return TaskStateChange.START;
		}

		if(oldSettings.getHostCleanUpTimer() != newSettings.getHostCleanUpTimer()) {
			this.hostCleanUpTimer = newSettings.getHostCleanUpTimer();
			change = true;
		}
		if(!oldSettings.getHostCleanUpTimerUnit().equals(newSettings.getHostCleanUpTimerUnit())) {
			this.hostCleanUpTimerUnit = newSettings.getHostCleanUpTimerUnit();
			change = true;
		}

		if(change) {
			return TaskStateChange.RESTART;
		}
		return TaskStateChange.NONE;
	}
}
