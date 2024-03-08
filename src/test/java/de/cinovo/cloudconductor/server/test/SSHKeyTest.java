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
class SSHKeyTest extends APITest {
	
	@Test
	void test() throws CloudConductorException {
		String firstContent = "SSH key content";
		String firstOwner = "foobar";
		String newContent = "key content 2";
		String newOwner = "admin";
		
		SSHKeyHandler handler = new SSHKeyHandler(this.getCSApi(), this.getToken());
		{
			Set<SSHKey> keys = handler.get();
			Assertions.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assertions.assertEquals(firstOwner, key.getOwner());
			Assertions.assertEquals(firstContent, key.getKey());
		}
		{
			SSHKey load = handler.get(firstOwner);
			Assertions.assertEquals(firstOwner, load.getOwner());
			Assertions.assertEquals(firstContent, load.getKey());
		}
		{
			SSHKey newKey = new SSHKey(newOwner, newContent);
			newKey.setUsername("root");
			handler.save(newKey);
		}
		{
			Set<SSHKey> keys2 = handler.get();
			Assertions.assertEquals(2, keys2.size());
			for (SSHKey sk : keys2) {
				if (sk.getOwner().equals(firstOwner)) {
					Assertions.assertEquals(firstContent, sk.getKey());
				} else if (sk.getOwner().equals(newOwner)) {
					Assertions.assertEquals(newContent, sk.getKey());
				} else {
					Assertions.fail();
				}
			}
		}
		{
			SSHKey load2 = handler.get(newOwner);
			Assertions.assertEquals(newOwner, load2.getOwner());
			Assertions.assertEquals(newContent, load2.getKey());
		}
		{
			handler.delete(newOwner);
		}
		{
			Set<SSHKey> keys = handler.get();
			Assertions.assertEquals(1, keys.size());
			SSHKey key = keys.iterator().next();
			Assertions.assertEquals(firstOwner, key.getOwner());
			Assertions.assertEquals(firstContent, key.getKey());
		}
	}
	
}
