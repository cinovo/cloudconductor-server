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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.lib.exceptions.ClientErrorException;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.exceptions.SerializationException;
import de.cinovo.cloudconductor.api.lib.exceptions.ServerErrorException;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Superclass for manager classes. Contains utility methods for the exclusive use by the concrete manager classes.
 * 
 * @author psigloch, mhilbert
 */
public abstract class AbstractApiHandler {
	
	/** object mapper for JSON (de)serialization */
	public static final ObjectMapper mapper = MapperFactory.createDefault();
	
	private static final String VAR_PATTERN = "\\{[a-zA-Z]+\\}";
	
	/** the URL of the config server */
	private String serverUrl;
	private String username;
	private String password;
	
	
	protected AbstractApiHandler(String cloudconductorUrl) {
		this.serverUrl = cloudconductorUrl;
	}
	
	protected AbstractApiHandler(String cloudconductorUrl, String username, String password) {
		this.serverUrl = cloudconductorUrl;
		this.username = username;
		this.password = password;
	}
	
	protected final Object _get(String path, JavaType type) throws CloudConductorException {
		HttpResponse response = this.request(path).get();
		AbstractApiHandler.assertSuccess(path, response);
		return this.objectFromResponse(response, type);
	}
	
	protected final <T> T _get(String path, Class<T> type) throws CloudConductorException {
		HttpResponse response = this.request(path).get();
		AbstractApiHandler.assertSuccess(path, response);
		return this.objectFromResponse(response, type);
	}
	
	protected final HttpResponse _put(String path) throws CloudConductorException {
		HttpResponse response = this.request(path).put();
		AbstractApiHandler.assertSuccess(path, response);
		return response;
	}
	
	protected final HttpResponse _put(String path, Object put) throws CloudConductorException {
		HttpResponse response = this.request(path, put).put();
		AbstractApiHandler.assertSuccess(path, response);
		return response;
	}
	
	protected final <T> T _put(String path, Object put, JavaType type) throws CloudConductorException {
		HttpResponse response = this.request(path, put, type).put();
		AbstractApiHandler.assertSuccess(path, response);
		if (type == null) {
			return null;
		}
		return this.objectFromResponse(response, type);
	}
	
	protected final <T> T _put(String path, Object put, Class<T> type) throws CloudConductorException {
		HttpResponse response = this._put(path, put);
		if (type == null) {
			return null;
		}
		return this.objectFromResponse(response, type);
	}
	
	protected final HttpResponse _post(String path, Object post) throws CloudConductorException {
		HttpResponse response = this.request(path, post).post();
		AbstractApiHandler.assertSuccess(path, response);
		return response;
	}
	
	protected final <T> T _post(String path, Object post, JavaType type) throws CloudConductorException {
		HttpResponse response = this.request(path, post, type).post();
		AbstractApiHandler.assertSuccess(path, response);
		if (type == null) {
			return null;
		}
		return this.objectFromResponse(response, type);
	}
	
	protected final <T> T _post(String path, Object post, Class<T> type) throws CloudConductorException {
		HttpResponse response = this._post(path, post);
		if (type == null) {
			return null;
		}
		return this.objectFromResponse(response, type);
	}
	
	protected final void _delete(String path) throws CloudConductorException {
		HttpResponse response = this.request(path).delete();
		AbstractApiHandler.assertSuccess(path, response);
	}
	
	/**
	 * Creates an object from the given HTTP response.
	 * 
	 * @param response the HTTP response
	 * @param type the expected Java type of the object
	 * @return the Java object
	 * @throws SerializationException if the mapping was unsuccessful
	 */
	protected final <T> T objectFromResponse(HttpResponse response, Class<T> type) throws SerializationException {
		try {
			return AbstractApiHandler.mapper.readValue(WS.getResponseAsString(response), type);
		} catch (IOException e) {
			throw new SerializationException("Failed to read object", e);
		}
	}
	
	/**
	 * Creates an object from the given HTTP response.
	 * 
	 * @param response the HTTP response
	 * @param type the expected Java type of the object
	 * @return the Java object
	 * @throws SerializationException if the mapping was unsuccessful
	 */
	protected final <T> T objectFromResponse(HttpResponse response, JavaType type) throws SerializationException {
		try {
			String value = WS.getResponseAsString(response);
			if (value.isEmpty()) {
				return null;
			}
			return AbstractApiHandler.mapper.readValue(value, type);
		} catch (IOException e) {
			throw new SerializationException("Failed to read object", e);
		}
	}
	
	/**
	 * Returns the plain text data returned in the given response.
	 * 
	 * @param response the HTTP response
	 * @return the plain text data.
	 */
	protected final String dataFromResponse(HttpResponse response) {
		return WS.getResponseAsString(response);
	}
	
	/**
	 * Creates HTTP request for the given path.
	 * 
	 * @param path the path to the resource
	 * @return the HTTP request
	 */
	protected final HTTPRequest request(String path) {
		if ((this.username != null) && (this.password != null)) {
			return WS.url(this.serverUrl + path).authBasic(this.username, this.password);
		}
		return WS.url(this.serverUrl + path);
	}
	
	/**
	 * Creates HTTP request for the given path with the given object as the body.
	 * 
	 * @param path the path to the resource
	 * @param obj the object
	 * @return the HTTP request
	 * @throws SerializationException if the object could not be mapped to a JSON string
	 */
	protected final HTTPRequest request(String path, Object obj) throws SerializationException {
		return this.request(path, obj, null);
	}
	
	protected final HTTPRequest request(String path, Object obj, JavaType type) throws SerializationException {
		try {
			ObjectWriter bodyWriter = AbstractApiHandler.mapper.writer();
			if ((type != null) && ((obj instanceof Set) || (obj instanceof List))) {
				bodyWriter = bodyWriter.withType(type);
			}
			String body = bodyWriter.writeValueAsString(obj);
			return this.request(path).body(body).header("Content-Type", MediaType.APPLICATION_JSON);
		} catch (IOException e) {
			throw new SerializationException();
		}
		
	}
	
	protected static final void assertSuccess(String path, HttpResponse response) throws ClientErrorException, ServerErrorException {
		switch (HttpStatusClass.get(response)) {
		case CLIENT_ERROR:
			throw new ClientErrorException(String.format("Client error (status: %d, request path: %s).", WS.getStatus(response), path));
		case SERVER_ERROR:
			throw new ServerErrorException(String.format("Server error (status: %d, request path: %s).", WS.getStatus(response), path));
		default:
			break;
		}
	}
	
	protected final JavaType getSetType(Class<?> clazz) {
		return AbstractApiHandler.mapper.getTypeFactory().constructCollectionType(Set.class, clazz);
	}
	
	protected String pathGenerator(String path, String... replace) {
		String[] split = path.split("/");
		if (split.length < 1) {
			return path;
		}
		StringBuffer buffer = new StringBuffer();
		String[] parts = split;
		if (split[0].isEmpty()) {
			parts = Arrays.copyOfRange(split, 1, split.length);
		}
		int counter = 0;
		for (String part : parts) {
			buffer.append("/");
			if (part.matches(AbstractApiHandler.VAR_PATTERN) && ((replace.length - 1) >= counter)) {
				buffer.append(replace[counter++]);
			} else {
				buffer.append(part);
			}
		}
		return buffer.toString();
	}
}
