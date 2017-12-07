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
import de.cinovo.cloudconductor.api.lib.manager.ConfigValueHandler;
import de.cinovo.cloudconductor.api.model.ConfigValue;
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
public class ConfigTest extends APITest {
	
	@Test
	public void loadConfigTemplate() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		Set<ConfigValue> config = h.getConfig("dev");
		Assert.assertNotNull(config);
	}
	
	@Test
	public void loadConfigService() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		Set<ConfigValue> config = h.getConfig("dev", "service1");
		Assert.assertNotNull(config);
	}
	
	@Test
	public void loadConfigKey() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		String config = h.getConfig("dev", "service1", "loggly.tags");
		Assert.assertNotNull(config);
		Assert.assertEquals("foo", config);
	}
	
	@Test
	public void testAddRemove() throws CloudConductorException {
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// Check config does not exist
			for(ConfigValue configValue : h.getConfig("dev")) {
				Assert.assertFalse(configValue.getKey().equals("foo"));
			}
			for(ConfigValue configValue : h.getConfig("dev", "svc")) {
				Assert.assertFalse(configValue.getKey().equals("foo"));
			}
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// add template level config
			h.addConfig("dev", "foo", "bar");
			Assert.assertEquals("bar", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// add service level config
			h.addConfig("dev", "svc", "foo", "baz");
			Assert.assertEquals("baz", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// remove service level config
			h.removeConfig("dev", "svc", "foo");
			Assert.assertEquals("bar", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// remove template level config
			h.removeConfig("dev", "", "foo");
			for(ConfigValue configValue : h.getConfig("dev")) {
				Assert.assertFalse(configValue.getKey().equals("foo"));
			}
			for(ConfigValue configValue : h.getConfig("dev", "svc")) {
				Assert.assertFalse(configValue.getKey().equals("foo"));
			}
		}
	}
}
