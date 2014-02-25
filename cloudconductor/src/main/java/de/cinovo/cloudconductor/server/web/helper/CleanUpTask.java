package de.cinovo.cloudconductor.server.web.helper;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;

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
	private IHostDAO dhost;
	
	
	@Override
	public void run() {
		if (this.dhost == null) {
			CleanUpTask.LOGGER.debug("Couldn't get the host dao injected");
			return;
		}
		List<EHost> hosts = this.dhost.findList();
		Set<EHost> delete = new HashSet<>();
		for (EHost host : hosts) {
			DateTime now = new DateTime();
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			if (diff > CleanUpTask.MAX_AGE) {
				delete.add(host);
			}
		}
		for (EHost host : delete) {
			this.dhost.delete(host);
		}
	}
	
}
