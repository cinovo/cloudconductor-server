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
import de.cinovo.cloudconductor.api.lib.manager.PackageHandler;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
@RunWith(SpringDaemonTestRunner.class)
@SuppressWarnings("javadoc")
public class PackageTest extends APITest {
	
	@Test
	public void testPackages() throws CloudConductorException {
		PackageHandler h = new PackageHandler(this.getCSApi(), this.getToken());
		{
			Set<Package> set = h.get();
			Assert.assertEquals(6, set.size());
		}
		{
			Package pkg = h.get("nginx");
			Assert.assertEquals("nginx", pkg.getName());
			Assert.assertEquals("Auto-generated from repository selected on 2013-09-04 14:20:08.", pkg.getDescription());
		}
	}
	
	@Test
	public void testVersion() throws CloudConductorException {
		PackageHandler h = new PackageHandler(this.getCSApi(), this.getToken());
		{
			Set<PackageVersion> rpms = h.getRPMS("nginx");
			Assert.assertEquals(1, rpms.size());
			PackageVersion pv = rpms.iterator().next();
			Assert.assertEquals("nginx", pv.getName());
			Assert.assertEquals("1.5.3-1", pv.getVersion());
		}
	}
}
