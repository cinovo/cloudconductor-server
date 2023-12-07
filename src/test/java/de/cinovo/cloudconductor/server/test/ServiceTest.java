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

import com.google.common.collect.Sets;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.ServiceHandler;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author hoegertn
 */
@ExtendWith(SpringDaemonExtension.class)
class ServiceTest extends APITest {

	@Test
	void test1() throws CloudConductorException {
		ServiceHandler h = new ServiceHandler(this.getCSApi(), this.getToken());
		{
			Set<Service> set = h.get();
			Assertions.assertEquals(3, set.size());
		}
		{
			Service svc = h.get("nginx");
			Assertions.assertEquals("nginx", svc.getName());
			Assertions.assertEquals("nginx", svc.getDescription());
		}
		{
			Service s = new Service();
			s.setId(1L);
			s.setName("svc1");
			s.setDescription("svc1");
			s.setInitScript("svc1");
			h.save(s);
			Set<Service> set = h.get();
			Assertions.assertEquals(4, set.size());
			Service svc = h.get("svc1");
			Assertions.assertEquals("svc1", svc.getName());
		}
		{
			Service s = new Service();
			s.setId(2L);
			s.setName("svc2");
			s.setDescription("svc2");
			s.setInitScript("svc2");
			s.setPackages(Sets.newHashSet("jdk"));
			h.save(s);
			Set<Service> set = h.get();
			Assertions.assertEquals(5, set.size());
			Service svc = h.get("svc2");
			Assertions.assertEquals("svc2", svc.getName());
			Assertions.assertEquals(1, svc.getPackages().size());
		}
		{
			h.delete("svc1");
			h.delete("svc2");
			Set<Service> set = h.get();
			Assertions.assertEquals(3, set.size());
		}
	}

}
