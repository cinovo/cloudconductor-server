package de.cinovo.cloudconductor.api.lib.helper;

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

import de.taimos.httputils.HTTPResponse;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Enumeration for the five HTTP status code classes (1xx, 2xx, 3xx, 4xx, 5xx).
 * 
 * @author psigloch, mhilbert
 */
public enum HttpStatusClass {
	/** informational status, 1xx codes */
	INFORMATIONAL(100, 200),
	/** success status, 2xx codes */
	SUCCESS(200, 300),
	/** redirection status, 3xx codes */
	REDIRECTION(300, 400),
	/** client error status, 4xx codes */
	CLIENT_ERROR(400, 500),
	/** server error status, 5xx codes */
	SERVER_ERROR(500, 600);
	
	private int min;
	private int max;
	
	
	private HttpStatusClass(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Returns the status code class of the given HTTP response.
	 * 
	 * @param response the HTTP response
	 * @return the status code class of the response
	 */
	public static final HttpStatusClass get(HTTPResponse response) {
		int status = response.getStatus();
		for (HttpStatusClass clazz : HttpStatusClass.values()) {
			if ((clazz.min <= status) && (clazz.max > status)) {
				return clazz;
			}
		}
		return null;
	}
}
