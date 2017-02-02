package de.cinovo.cloudconductor.api;

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

/**
 * Media types used and supported by the config server <br>
 * <br>
 * Copyright 2017 Cinovo AG<br>
 *
 * @author psigloch
 */
public interface MediaType {

	/**
	 * JSON media type
	 */
	static final String APPLICATION_JAVAPROPS = "application/x-javaprops";
	/**
	 * JSON media type
	 */
	static final String APPLICATION_JAVAARGS = "application/x-javaargs";
	/**
	 * JSON media type
	 */
	static final String APPLICATION_JSON = "application/json;charset=UTF-8";
	/**
	 * HTML media type
	 */
	static final String TEXT_HTML = "text/html;charset=UTF-8";
}
