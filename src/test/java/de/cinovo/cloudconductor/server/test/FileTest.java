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
import de.cinovo.cloudconductor.api.lib.manager.ConfigFileHandler;
import de.cinovo.cloudconductor.api.model.ConfigFile;
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
public class FileTest extends APITest {
	
	@Test
	public void test() throws CloudConductorException {
		ConfigFileHandler h = new ConfigFileHandler(this.getCSApi());
		{
			Set<ConfigFile> set = h.get();
			Assert.assertEquals(1, set.size());
			ConfigFile file = set.iterator().next();
			Assert.assertEquals("file1", file.getName());
			Assert.assertEquals("/root/foo", file.getTargetPath());
			Assert.assertEquals("root", file.getOwner());
		}
		{
			ConfigFile file = h.get("file1");
			Assert.assertEquals("file1", file.getName());
			Assert.assertEquals("/root/foo", file.getTargetPath());
			Assert.assertEquals("root", file.getOwner());
		}
		{
			String data = h.getData("file1");
			Assert.assertEquals("Testfile for root", data);
		}
		{
			ConfigFile cf = new ConfigFile("file2", "nginx", "/root/bar", "root", "root", "700", false, false, "", null);
			h.save(cf);
			Set<ConfigFile> set = h.get();
			Assert.assertEquals(2, set.size());
		}
		{
			ConfigFile file = h.get("file2");
			Assert.assertEquals("file2", file.getName());
			Assert.assertEquals("/root/bar", file.getTargetPath());
			Assert.assertEquals("root", file.getOwner());
		}
		{
			h.saveData("file2", "Testcontent 2");
		}
		{
			String data = h.getData("file2");
			Assert.assertEquals("Testcontent 2", data);
		}
		
	}
}
