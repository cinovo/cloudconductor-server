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

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.HostHandler;
import de.cinovo.cloudconductor.api.lib.manager.ServiceHandler;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author hoegertn
 */
@RunWith(SpringDaemonTestRunner.class)
@SuppressWarnings("javadoc")
public class HostTest extends APITest {
	
	private static final String TEST_SERVICE = "nginx";
	private static final String HOST22 = "host2";
	private static final String HOST1 = "host1";
	
	
	@Test
	public void test1() throws CloudConductorException {
		HostHandler h = new HostHandler(this.getCSApi());
		{
			Set<Host> hosts = h.get();
			Assert.assertEquals(1, hosts.size());
			Host next = hosts.iterator().next();
			Assert.assertEquals(HostTest.HOST1, next.getName());
		}
		{
			Host host = h.get(HostTest.HOST1);
			Assert.assertNotNull(host);
			Assert.assertEquals(HostTest.HOST1, host.getName());
		}
		{
			Host host = new Host();
			host.setName(HostTest.HOST22);
			host.setAgent(HostTest.HOST22);
			host.setTemplate("dev");
			h.save(host);
			Set<Host> hosts = h.get();
			Assert.assertEquals(2, hosts.size());
			Host host2 = h.get(HostTest.HOST22);
			Assert.assertNotNull(host2);
			Assert.assertEquals(HostTest.HOST22, host2.getName());
		}
		{
			h.delete(HostTest.HOST22);
			Set<Host> hosts = h.get();
			Assert.assertEquals(1, hosts.size());
		}
	}
	
	@Test
	public void testServices() throws CloudConductorException {
		HostHandler h = new HostHandler(this.getCSApi());
		{
			Set<Service> services = h.getServices(HostTest.HOST1);
			Assert.assertEquals(0, services.size());
		}
		{
			Boolean inSync = h.inSync(HostTest.HOST1);
			Assert.assertEquals(false, inSync);
		}
		{
			ServiceHandler sh = new ServiceHandler(this.getCSApi());
			h.setService(HostTest.HOST1, sh.get(HostTest.TEST_SERVICE));
			this.assertState(h, ServiceState.STOPPED);
		}
		{
			h.startService(HostTest.HOST1, HostTest.TEST_SERVICE);
			this.assertState(h, ServiceState.STARTING);
		}
		{
			h.stopService(HostTest.HOST1, HostTest.TEST_SERVICE);
			this.assertState(h, ServiceState.STOPPING);
		}
		{
			h.restartService(HostTest.HOST1, HostTest.TEST_SERVICE);
			this.assertState(h, ServiceState.RESTARTING_STOPPING);
		}
		{
			h.removeService(HostTest.HOST1, HostTest.TEST_SERVICE);
			Set<Service> services = h.getServices(HostTest.HOST1);
			Assert.assertEquals(0, services.size());
		}
	}
	
	private void assertState(HostHandler h, ServiceState state) throws CloudConductorException {
		Set<Service> services = h.getServices(HostTest.HOST1);
		Assert.assertEquals(1, services.size());
		Service svc = services.iterator().next();
		Assert.assertEquals(HostTest.TEST_SERVICE, svc.getName());
		Assert.assertEquals(state, svc.getState());
	}
}
