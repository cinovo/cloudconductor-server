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
import de.cinovo.cloudconductor.api.model.SSHKey;
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
public class SSHKeyTest extends APITest {
	
	@Test
	public void test() throws CloudConductorException {
		String firstContent = "SSH key content";
		String firstOwner = "foobar";
		String newContent = "key content 2";
		String newOwner = "admin";
		
		SSHKeyHandler handler = new SSHKeyHandler(this.getCSApi(), this.getToken());
		{
			Set<SSHKey> keys = handler.get();
			Assert.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assert.assertEquals(firstOwner, key.getOwner());
			Assert.assertEquals(firstContent, key.getKey());
		}
		{
			SSHKey load = handler.get(firstOwner);
			Assert.assertEquals(firstOwner, load.getOwner());
			Assert.assertEquals(firstContent, load.getKey());
		}
		{
			SSHKey newKey = new SSHKey(newOwner, newContent);
			newKey.setUsername("root");
			handler.save(newKey);
		}
		{
			Set<SSHKey> keys2 = handler.get();
			Assert.assertEquals(2, keys2.size());
			for (SSHKey sk : keys2) {
				if (sk.getOwner().equals(firstOwner)) {
					Assert.assertEquals(firstContent, sk.getKey());
				} else if (sk.getOwner().equals(newOwner)) {
					Assert.assertEquals(newContent, sk.getKey());
				} else {
					Assert.fail();
				}
			}
		}
		{
			SSHKey load2 = handler.get(newOwner);
			Assert.assertEquals(newOwner, load2.getOwner());
			Assert.assertEquals(newContent, load2.getKey());
		}
		{
			handler.delete(newOwner);
		}
		{
			Set<SSHKey> keys = handler.get();
			Assert.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assert.assertEquals(firstOwner, key.getOwner());
			Assert.assertEquals(firstContent, key.getKey());
		}
	}
	
}
