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

import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.PackageHandler;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
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
public class IOModuleTest extends APITest {
	
	@Test
	public void test() throws CloudConductorException {
		PackageHandler h = new PackageHandler(this.getCSApi());
		{
			Set<Package> packages = h.get();
			Assert.assertEquals(5, packages.size());
		}
		
		{
			PackageVersion[] set = new PackageVersion[2];
			set[0] = new PackageVersion("package1", "1.0.0", null);
			set[1] = new PackageVersion("package2", "1.2.0", null);
			HttpResponse post = this.json(this.api("/io/versions"), set).post();
			this.assertOK(post);
		}
		
		{
			Set<Package> packages = h.get();
			Assert.assertEquals(7, packages.size());
		}
	}
}
