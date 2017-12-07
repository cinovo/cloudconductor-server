package de.cinovo.cloudconductor.api.lib.helper;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import com.fasterxml.jackson.databind.JavaType;
import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @param <T> the model class
 * @author psigloch
 */
public abstract class DefaultRestHandler<T extends INamed> extends AbstractApiHandler {

	protected DefaultRestHandler(String cloudconductorUrl, String token) {
		super(cloudconductorUrl, token);
	}

	protected abstract String getDefaultPath();

	protected abstract Class<T> getAPIClass();

	protected JavaType getSetType() {
		return this.getSetType(this.getAPIClass());
	}

	/**
	 * @return set of T
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<T> get() throws CloudConductorException {
		return (Set<T>) this._get(this.getDefaultPath(), this.getSetType());
	}

	/**
	 * @param apiObject T
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void save(T apiObject) throws CloudConductorException {
		String path = this.getDefaultPath();
		this._put(path, apiObject);
	}

	/**
	 * @param name the identifier of the object T
	 * @return T
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public T get(String name) throws CloudConductorException {
		String path = this.pathGenerator("/{name}", name);
		return this._get(path, this.getAPIClass());
	}

	/**
	 * @param name the identifier of the object T
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void delete(String name) throws CloudConductorException {
		String path = this.pathGenerator("/{name}", name);
		this._delete(path);
	}

	@Override
	protected final String pathGenerator(String path, String... replace) {
		String s = this.getDefaultPath() + path;
		return super.pathGenerator(s, replace);
	}
}
