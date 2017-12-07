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

import de.cinovo.cloudconductor.api.lib.helper.AuthHandler;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfiguration;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;

import java.util.concurrent.TimeUnit;

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

	private String token;

	protected APITest() {
		this.token = new AuthHandler(this.getCSApi()).auth();
	}

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

	protected final String getToken() {
		return this.token;
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

	protected void delay() {
		try {
			TimeUnit.MILLISECONDS.sleep(250);
		} catch(InterruptedException e) {
			return;
		}
	}
	

}
