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

import com.google.common.collect.Sets;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.ServiceHandler;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.springcxfdaemon.SpringDaemonTestRunner;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
@RunWith(SpringDaemonTestRunner.class)
public class ServiceTest extends APITest {
	
	@Test
	public void test1() throws CloudConductorException {
		ServiceHandler h = new ServiceHandler(this.getCSApi());
		{
			Set<Service> set = h.get();
			Assert.assertEquals(3, set.size());
		}
		{
			Service svc = h.get("nginx");
			Assert.assertEquals("nginx", svc.getName());
			Assert.assertEquals("nginx", svc.getDescription());
		}
		{
			Service s = new Service("svc1", "svc1", "svc1", ServiceState.STOPPED, null);
			h.save(s);
			Set<Service> set = h.get();
			Assert.assertEquals(4, set.size());
			Service svc = h.get("svc1");
			Assert.assertEquals("svc1", svc.getName());
		}
		{
			Service s = new Service("svc2", "svc2", "svc2", ServiceState.STOPPED, Sets.newHashSet("jdk"));
			h.save(s);
			Set<Service> set = h.get();
			Assert.assertEquals(5, set.size());
			Service svc = h.get("svc2");
			Assert.assertEquals("svc2", svc.getName());
			Assert.assertEquals(1, svc.getPackages().size());
		}
		{
			h.delete("svc1");
			h.delete("svc2");
			Set<Service> set = h.get();
			Assert.assertEquals(3, set.size());
		}
	}
	
	@Test
	public void testRPM() throws CloudConductorException {
		ServiceHandler h = new ServiceHandler(this.getCSApi());
		{
			Set<Package> pkgs = h.getPackages("nginx");
			Assert.assertEquals(1, pkgs.size());
			Package pv = pkgs.iterator().next();
			Assert.assertEquals("nginx", pv.getName());
		}
		{
			h.addPackage("nginx", "jdk");
			Set<Package> rpms = h.getPackages("nginx");
			Assert.assertEquals(2, rpms.size());
		}
		{
			h.removePackage("nginx", "jdk");
		}
		{
			Set<Package> pkgs = h.getPackages("nginx");
			Assert.assertEquals(1, pkgs.size());
		}
	}
}
