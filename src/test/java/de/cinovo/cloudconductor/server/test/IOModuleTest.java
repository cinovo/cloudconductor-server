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

import de.cinovo.cloudconductor.api.DependencyType;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.IOModuleHandler;
import de.cinovo.cloudconductor.api.lib.manager.PackageHandler;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author hoegertn
 */
@RunWith(SpringDaemonTestRunner.class)
@SuppressWarnings("javadoc")
public class IOModuleTest extends APITest {

	@Test
	public void test() throws CloudConductorException {
		PackageHandler h = new PackageHandler(this.getCSApi());
		IOModuleHandler ioH = new IOModuleHandler(this.getCSApi());
		{
			Set<Package> packages = h.get();
			Assert.assertEquals(6, packages.size());
		}

		{
			HashSet<Dependency> test = new HashSet<Dependency>();
			Dependency dep = new Dependency();
			dep.setName("asd");
			dep.setVersion("4.1.1");
			dep.setOperator("");
			dep.setType(DependencyType.REQUIRES);
			;

			test.add(dep);
			Set<PackageVersion> set = new HashSet<PackageVersion>();
			PackageVersion pv1 = new PackageVersion();
			pv1.setName("package1");
			pv1.setVersion("1.0.0");
			pv1.setRepos(new HashSet<String>());
			pv1.getRepos().add("TESTREPO");

			PackageVersion pv2 = new PackageVersion();
			pv2.setName("package1");
			pv2.setVersion("1.2.0");
			pv2.setRepos(new HashSet<String>());
			pv2.getRepos().add("TESTREPO");

			set.add(pv1);
			set.add(pv2);
			ioH.importPackages(set);
		}

		{
			Set<Package> packages = h.get();
			Assert.assertEquals(7, packages.size());
		}
	}
}
