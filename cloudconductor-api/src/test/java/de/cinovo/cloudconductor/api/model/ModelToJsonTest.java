package de.cinovo.cloudconductor.api.model;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cinovo.cloudconductor.api.DependencyType;
import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ModelToJsonTest {
	
	private ObjectMapper mapper = MapperFactory.createDefault();
	
	
	/**
	 * @throws JsonProcessingException on error
	 */
	@Test
	public void singleObjects() throws JsonProcessingException {
		
		SSHKey key = new SSHKey("akey", "aowner");
		System.out.println(this.mapper.writeValueAsString(key));
		
		Set<SSHKey> keys = new HashSet<>();
		keys.add(key);
		System.out.println(this.mapper.writeValueAsString(keys));
		System.out.println(this.mapper.writeValueAsString(keys.toArray(new SSHKey[0])));
		
		HashSet<Dependency> hashSet = new HashSet<Dependency>();
		hashSet.add(new Dependency("moep", "moep", "<", DependencyType.PROVIDES.toString()));
		PackageVersion v = new PackageVersion("test", "test", hashSet);
		System.out.println(this.mapper.writeValueAsString(v));
	}
}
