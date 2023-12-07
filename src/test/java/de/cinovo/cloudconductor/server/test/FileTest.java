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
import de.cinovo.cloudconductor.api.lib.manager.ConfigFileHandler;
import de.cinovo.cloudconductor.api.model.ConfigFile;
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
class FileTest extends APITest {
	
	@Test
	void test() throws CloudConductorException {
		ConfigFileHandler h = new ConfigFileHandler(this.getCSApi(), this.getToken());
		{
			Set<ConfigFile> set = h.get();
			Assertions.assertEquals(1, set.size());
			ConfigFile file = set.iterator().next();
			Assertions.assertEquals("file1", file.getName());
			Assertions.assertEquals("/root/foo", file.getTargetPath());
			Assertions.assertEquals("root", file.getOwner());
		}
		{
			ConfigFile file = h.get("file1");
			Assertions.assertEquals("file1", file.getName());
			Assertions.assertEquals("/root/foo", file.getTargetPath());
			Assertions.assertEquals("root", file.getOwner());
		}
		{
			String data = h.getData("file1");
			Assertions.assertEquals("Testfile for root", data);
		}
		{
			ConfigFile cf = new ConfigFile();
			cf.setName("file2");
			cf.setPkg("nginx");
			cf.setFileMode("700");
			cf.setChecksum("");
			cf.setGroup("root");
			cf.setOwner("root");
			cf.setReloadable(false);
			cf.setTemplate(false);
			cf.setTargetPath("/root/bar");
			cf.setPkg(null);

			h.save(cf);
			Set<ConfigFile> set = h.get();
			Assertions.assertEquals(2, set.size());
		}
		{
			ConfigFile file = h.get("file2");
			Assertions.assertEquals("file2", file.getName());
			Assertions.assertEquals("/root/bar", file.getTargetPath());
			Assertions.assertEquals("root", file.getOwner());
		}
		{
			h.saveData("file2", "Testcontent 2");
		}
		{
			String data = h.getData("file2");
			Assertions.assertEquals("Testcontent 2", data);
		}
		
	}
}
