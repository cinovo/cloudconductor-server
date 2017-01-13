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
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Service("cleanuptask")
public class CleanUpTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CleanUpTask.class);
	private static final int MAX_AGE = 180;
	
	@Autowired
	private IHostDAO hostDAO;
	
	
	@Override
	public void run() {
		this.cleanUpHosts();
		//TODO: Find other things which may be cleaned up periodicly
	}


	private void cleanUpHosts() {
		List<EHost> hosts = this.hostDAO.findList();
		for (EHost host : hosts) {
			DateTime now = new DateTime();
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			if (diff > CleanUpTask.MAX_AGE) {
				this.hostDAO.delete(host);
			}
		}
	}
	
}
