/**
 * 
 */
package de.cinovo.cloudconductor.server.impl.rest;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;

import de.cinovo.cloudconductor.api.model.INamed;
import de.cinovo.cloudconductor.server.dao.IFindNamed;
import de.taimos.dao.IEntity;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public abstract class ImplHelper {
	
	/**
	 * @param <T> the type of the model object
	 * @param model the model object
	 */
	protected final <T extends IEntity<?>> void assertModelFound(T model) {
		if (model == null) {
			throw new NotFoundException();
		}
	}
	
	protected final void assertName(String name, INamed object) {
		RESTAssert.assertNotNull(object);
		RESTAssert.assertEquals(name, object.getName());
	}
	
	protected final <T extends IEntity<?>> T findByName(IFindNamed<T> dao, String name) {
		T found = dao.findByName(name);
		this.assertModelFound(found);
		return found;
	}
	
	protected final <T extends IEntity<?>> List<T> findByName(IFindNamed<T> dao, Set<String> names) {
		List<T> found = new ArrayList<>();
		for (String s : names) {
			T t = dao.findByName(s);
			this.assertModelFound(t);
			found.add(t);
		}
		return found;
	}
	
}
