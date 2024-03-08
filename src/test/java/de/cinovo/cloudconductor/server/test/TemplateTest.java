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
import de.cinovo.cloudconductor.api.lib.manager.SSHKeyHandler;
import de.cinovo.cloudconductor.api.lib.manager.TemplateHandler;
import de.cinovo.cloudconductor.api.model.HostIdentifier;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author hoegertn
 */
@ExtendWith(SpringDaemonExtension.class)
class TemplateTest extends APITest {

	private static final String TEMPLATE = "dev";

	private final TemplateHandler h = new TemplateHandler(this.getCSApi(), this.getToken());

	@Test
	void testBasic() throws CloudConductorException {
		this.delay();
		{
			Set<Template> set = this.h.get();
			Assertions.assertEquals(2, set.size());
		}
		{
			Template template = this.h.get(TemplateTest.TEMPLATE);
			Assertions.assertEquals(TemplateTest.TEMPLATE, template.getName());
		}
		{
			Set<HostIdentifier> servers = new HashSet<>();
			servers.add(new HostIdentifier("localhost", "123123"));
			Template t = new Template();
			t.setName("template2");
			t.setDescription("new template");
			t.setHosts(servers);

			this.h.save(t);
			Set<Template> set = this.h.get();
			Assertions.assertEquals(3, set.size());
			Template template = this.h.get("template2");
			Assertions.assertEquals("template2", template.getName());
		}
		{
			this.h.delete("template2");
			Set<Template> set = this.h.get();
			Assertions.assertEquals(2, set.size());
		}
	}

	@Test
	void testPackageUpdate() throws CloudConductorException {
		this.delay();
		{
			Set<PackageVersion> versions = this.h.getVersions(TemplateTest.TEMPLATE);
			Assertions.assertEquals(5, versions.size());
		}
	}

	@Test
	void testSSH() throws CloudConductorException {
		this.delay();
		SSHKeyHandler ssh = new SSHKeyHandler(this.getCSApi(), this.getToken());
		{
			Set<SSHKey> keys = this.h.getSSHKeys(TemplateTest.TEMPLATE);
			Assertions.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assertions.assertEquals("foobar", key.getOwner());
		}
		{
			ssh.delete("foobar");
			Set<SSHKey> hosts = this.h.getSSHKeys(TemplateTest.TEMPLATE);
			Assertions.assertEquals(0, hosts.size());
		}
		{
			SSHKey key = new SSHKey();
			key.setKey("foobar");
			key.setUsername("root");
			key.setOwner("foobar");
			List<String> templates = new ArrayList<>();
			templates.add(TemplateTest.TEMPLATE);
			key.setTemplates(templates);
			ssh.save(key);
			Set<SSHKey> keys = this.h.getSSHKeys(TemplateTest.TEMPLATE);
			Assertions.assertEquals(1, keys.size());
			key = keys.iterator().next();
			Assertions.assertEquals("foobar", key.getOwner());
		}
	}

	@Test
	void testServices() throws CloudConductorException {
		this.delay();
		{
			Set<Service> services = this.h.getServices(TemplateTest.TEMPLATE);
			Assertions.assertEquals(2, services.size());
		}
	}
}
