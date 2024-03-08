package de.cinovo.cloudconductor.server.test;

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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.HostHandler;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author hoegertn
 */
@ExtendWith(SpringDaemonExtension.class)
class HostTest extends APITest {
	
	private static final String HOST1 = "host1";
	
	
	@Test
	void test1() throws CloudConductorException {
		HostHandler h = new HostHandler(this.getCSApi(), this.getToken());
		{
			Set<Host> hosts = h.get();
			Assertions.assertEquals(1, hosts.size());
			Host next = hosts.iterator().next();
			Assertions.assertEquals(HostTest.HOST1, next.getName());
		}
		{
			Host host = h.get("123123-123123-123123");
			Assertions.assertNotNull(host);
			Assertions.assertEquals(HostTest.HOST1, host.getName());
		}
	}
}
