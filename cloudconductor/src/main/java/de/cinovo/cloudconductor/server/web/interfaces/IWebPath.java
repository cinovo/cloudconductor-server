package de.cinovo.cloudconductor.server.web.interfaces;

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

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IWebPath {
	
	public static final String WEBROOT = "/web";
	/**
	 * the default view
	 */
	public static final String DEFAULTVIEW = "/";
	
	// -------------------------------------------------------
	// VARIABLES
	// -------------------------------------------------------
	
	/** variable for name */
	public static final String VAR_NAME = "name";
	/** variable for template name */
	public static final String VAR_TEMPLATE = "template";
	/** variable for host name */
	public static final String VAR_HOST = "host";
	/** variable for service name */
	public static final String VAR_SERVICE = "service";
	/** variable for key */
	public static final String VAR_KEY = "key";
	/** variable for key */
	public static final String VAR_VERSION = "version";
	/** variable for package name */
	public static final String VAR_PKG = "pkg";
	/** variable for id's */
	public static final String VAR_ID = "id";
	// -------------------------------------------------------
	// ACTIONS
	// -------------------------------------------------------
	
	public static final String ACTION_ADD = "/add";
	public static final String ACTION_DELETE = "/delete";
	public static final String ACTION_REMOVE = "/remove";
	public static final String ACTION_EDIT = "/edit";
	public static final String ACTION_SAVE = "/save";
}
