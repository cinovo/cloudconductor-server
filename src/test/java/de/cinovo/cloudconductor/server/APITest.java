package de.cinovo.cloudconductor.server;

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

import org.apache.http.HttpResponse;
import org.junit.Assert;

import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfiguration;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 		
 */
@RunnerConfiguration(config = TestConfig.class, svc = "cloudconductor", loggingConfigurer = Log4jLoggingConfigurer.class)
public abstract class APITest {
	
	/**
	 * create call to http://server/<path>
	 * 
	 * @param path the path
	 * @return the HTTPRequest
	 */
	protected final HTTPRequest url(String path) {
		return WS.url(this.getCSUrl() + path);
	}
	
	/**
	 * @return the URL of the Test-cloudconductor
	 */
	protected final String getCSUrl() {
		String port = System.getProperty("svc.port");
		return "http://localhost:" + port;
	}
	
	/**
	 * @return the API-URL of the Test-cloudconductor
	 */
	protected final String getCSApi() {
		return this.getCSUrl() + "/api";
	}
	
	/**
	 * create call to http://server/api/<path>
	 * 
	 * @param path the path
	 * @return the HTTPRequest
	 */
	protected final HTTPRequest api(String path) {
		return this.url("/api" + path);
	}
	
	/**
	 * create call to http://server/web/<path>
	 * 
	 * @param path the path
	 * @return the HTTPRequest
	 */
	protected final HTTPRequest web(String path) {
		return this.url("/web" + path);
	}
	
	/**
	 * assert that the response has a status 2XX
	 * 
	 * @param res the HttpResponse to check
	 */
	protected final void assertOK(HttpResponse res) {
		Assert.assertTrue("Status: " + res.getStatusLine().getStatusCode(), WS.isStatusOK(res));
	}
	
	/**
	 * assert that the response has the given status
	 * 
	 * @param res the HttpResponse to check
	 * @param status the status to assert
	 */
	protected final void assertStatus(HttpResponse res, int status) {
		Assert.assertEquals("Status: " + res.getStatusLine().getStatusCode(), status, WS.getStatus(res));
	}
	
	/**
	 * add the given object as JSON and set content type
	 * 
	 * @param req the HTTPRequest
	 * @param o the object to send
	 * @return the enriched HTTPRequest
	 */
	protected final HTTPRequest json(HTTPRequest req, Object o) {
		try {
			return req.contentType("application/json;charset=UTF-8").body(MapperFactory.createDefault().writeValueAsString(o));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * read the given HttpResponse as JSON and convert into given object
	 * 
	 * @param <T> content type
	 * @param res the HttpResponse
	 * @param clazz the class to deserialize into
	 * @return the deserialized object
	 */
	protected final <T> T readAsObject(HttpResponse res, Class<T> clazz) {
		try {
			return MapperFactory.createDefault().readValue(res.getEntity().getContent(), clazz);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
