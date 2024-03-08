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
import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
@ExtendWith(SpringDaemonExtension.class)
class ConfigTest extends APITest {
	
	@Test
	void loadConfigTemplate() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		Set<ConfigValue> config = h.getConfig("dev");
		Assertions.assertNotNull(config);
	}
	
	@Test
	void loadConfigService() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		Set<ConfigValue> config = h.getConfig("dev", "service1");
		Assertions.assertNotNull(config);
	}
	
	@Test
	void loadConfigKey() throws CloudConductorException {
		ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
		String config = h.getConfig("dev", "service1", "loggly.tags");
		Assertions.assertNotNull(config);
		Assertions.assertEquals("foo", config);
	}
	
	@Test
	void testAddRemove() throws CloudConductorException {
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// Check config does not exist
			for(ConfigValue configValue : h.getConfig("dev")) {
				Assertions.assertNotEquals("foo", configValue.getKey());
			}
			for(ConfigValue configValue : h.getConfig("dev", "svc")) {
				Assertions.assertNotEquals("foo", configValue.getKey());
			}
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// add template level config
			h.addConfig("dev", "foo", "bar");
			Assertions.assertEquals("bar", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// add service level config
			h.addConfig("dev", "svc", "foo", "baz");
			Assertions.assertEquals("baz", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// remove service level config
			h.removeConfig("dev", "svc", "foo");
			Assertions.assertEquals("bar", h.getConfig("dev", "svc", "foo"));
		}
		{
			ConfigValueHandler h = new ConfigValueHandler(this.getCSApi(), this.getToken());
			// remove template level config
			h.removeConfig("dev", null, "foo");
			for(ConfigValue configValue : h.getConfig("dev")) {
				Assertions.assertNotEquals("foo", configValue.getKey());
			}
			for(ConfigValue configValue : h.getConfig("dev", "svc")) {
				Assertions.assertNotEquals("foo", configValue.getKey());
			}
		}
	}
}
