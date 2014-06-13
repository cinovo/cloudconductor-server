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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.PackageHandler;
import de.cinovo.cloudconductor.api.lib.manager.TemplateHandler;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;
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
@SuppressWarnings("javadoc")
public class TemplateTest extends APITest {
	
	private static final String TEMPLATE = "dev";
	
	
	@Test
	public void testBasic() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		{
			Set<Template> set = h.get();
			Assert.assertEquals(1, set.size());
			Template next = set.iterator().next();
			Assert.assertEquals(TemplateTest.TEMPLATE, next.getName());
		}
		{
			Template template = h.get(TemplateTest.TEMPLATE);
			Assert.assertEquals(TemplateTest.TEMPLATE, template.getName());
		}
		{
			Template t = new Template("template2", "new template", "localhost", null, null, null, null);
			h.save(t);
			Set<Template> set = h.get();
			Assert.assertEquals(2, set.size());
			Template template = h.get("template2");
			Assert.assertEquals("template2", template.getName());
		}
		{
			h.delete("template2");
			Set<Template> set = h.get();
			Assert.assertEquals(1, set.size());
		}
	}
	
	@Test
	public void testHost() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		Set<Host> hosts = h.getHosts(TemplateTest.TEMPLATE);
		Assert.assertEquals(1, hosts.size());
		Host host = hosts.iterator().next();
		Assert.assertEquals("host1", host.getName());
	}
	
	@Test
	public void testSSH() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		{
			Set<SSHKey> keys = h.getSSHKeys(TemplateTest.TEMPLATE);
			Assert.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assert.assertEquals("foobar", key.getOwner());
		}
		{
			h.removeSSHKey(TemplateTest.TEMPLATE, "foobar");
			Set<SSHKey> hosts = h.getSSHKeys(TemplateTest.TEMPLATE);
			Assert.assertEquals(0, hosts.size());
		}
		{
			h.addSSHKey(TemplateTest.TEMPLATE, "foobar");
			Set<SSHKey> keys = h.getSSHKeys(TemplateTest.TEMPLATE);
			Assert.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assert.assertEquals("foobar", key.getOwner());
		}
	}
	
	@Test
	public void testPackageVersions() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		{
			Set<PackageVersion> versions = h.getVersions(TemplateTest.TEMPLATE);
			Assert.assertEquals(5, versions.size());
		}
		{
			h.removeVersion(TemplateTest.TEMPLATE, new PackageHandler(this.getCSApi()).getRPMS("nginx").iterator().next());
			// Works only sometimes
			// Set<PackageVersion> versions = h.getVersions(TemplateTest.TEMPLATE);
			// Assert.assertEquals(4, versions.size());
		}
		{
			PackageVersion pv = new PackageVersion("nginx", "1.5.3-1", null);
			h.addVersion(TemplateTest.TEMPLATE, pv);
			Set<PackageVersion> versions = h.getVersions(TemplateTest.TEMPLATE);
			Assert.assertEquals(5, versions.size());
		}
	}
	
	@Test
	public void testPackageUpdate() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		{
			Set<PackageVersion> versions = h.getVersions(TemplateTest.TEMPLATE);
			Assert.assertEquals(5, versions.size());
		}
		{
			PackageVersion pv = new PackageVersion("nginx", "1.5.4-1", null);
			new PackageHandler(this.getCSApi()).addRPM("nginx", pv);
			
			h.addVersion(TemplateTest.TEMPLATE, pv);
			Set<PackageVersion> versions = h.getVersions(TemplateTest.TEMPLATE);
			Assert.assertEquals(5, versions.size());
		}
	}
	
	@Test
	public void testServices() throws CloudConductorException {
		TemplateHandler h = new TemplateHandler(this.getCSApi());
		{
			Set<Service> services = h.getServices(TemplateTest.TEMPLATE);
			Assert.assertEquals(2, services.size());
		}
	}
}
