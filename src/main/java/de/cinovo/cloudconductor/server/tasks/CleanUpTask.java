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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service()
public class CleanUpTask implements IServerTasks {

	private static final int MAX_AGE = 180;

	@Autowired
	private IHostDAO hostDAO;


	@Override
	public void run() {
		this.cleanUpHosts();
	}


	private void cleanUpHosts() {
		List<EHost> hosts = this.hostDAO.findList();
		for(EHost host : hosts) {
			DateTime now = new DateTime();
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			if(diff > CleanUpTask.MAX_AGE) {
				this.hostDAO.delete(host);
			}
		}
	}

	@Override
	public String getTaskIdentifier() {
		return "CLEAN_UP_TASK";
	}

	@Override
	public void create(EServerOptions settings) {
		SchedulerService.instance.register(this.getTaskIdentifier(), this, settings.getHostCleanUpTimer(), settings.getHostCleanUpTimerUnit());
	}

	@Override
	public void update(EServerOptions oldSettings, EServerOptions newSettings) {
		boolean change = oldSettings == null;
		if(oldSettings.getHostCleanUpTimer() != newSettings.getHostCleanUpTimer()) {
			change = true;
		}
		if(!oldSettings.getHostCleanUpTimerUnit().equals(newSettings.getHostCleanUpTimerUnit())) {
			change = true;
		}
		if(change) {
			SchedulerService.instance.resetTask(this.getTaskIdentifier(), newSettings.getHostCleanUpTimer(), newSettings.getHostCleanUpTimerUnit());
		}
	}
}
