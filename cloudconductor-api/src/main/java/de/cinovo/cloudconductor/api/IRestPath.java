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
 * Path's provided and supported by the api
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IRestPath {
	
	/**
	 * current directory "/"
	 */
	public static final String ROOT = "/";
	
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
	/** variable for package name */
	public static final String VAR_PKG = "pkg";
	/** variable for version name */
	public static final String VAR_VERSION = "version";
	
	// -------------------------------------------------------
	// DEFAULT
	// -------------------------------------------------------
	
	/**
	 * the default identifier for a name
	 */
	public static final String DEFAULT_NAME = "/{" + IRestPath.VAR_NAME + "}";
	
	// -------------------------------------------------------
	// AGENT
	// -------------------------------------------------------
	
	/**
	 * agent api
	 */
	public static final String AGENT = "/agent";
	
	/**
	 * notify package state for template and host
	 */
	public static final String AGENT_PACKAGE_STATE = "/{" + IRestPath.VAR_TEMPLATE + "}/{" + IRestPath.VAR_HOST + "}/package";
	
	/**
	 * notify service state for template and host
	 */
	public static final String AGENT_SERVICE_STATE = "/{" + IRestPath.VAR_TEMPLATE + "}/{" + IRestPath.VAR_HOST + "}/service";
	
	// -------------------------------------------------------
	// CONFIG FILES
	// -------------------------------------------------------
	/**
	 * configfile api
	 */
	public static final String FILE = "/file";
	
	/**
	 * interact with the data of a configfile
	 */
	public static final String FILE_DATA = "/{" + IRestPath.VAR_NAME + "}/data";
	
	// -------------------------------------------------------
	// CONFIG VALUES
	// -------------------------------------------------------
	
	/**
	 * config value api
	 */
	public static final String CONFIG = "/config";
	/**
	 * interact with the config of a template
	 */
	public static final String CONFIG_TEMPLATE = "/{" + IRestPath.VAR_TEMPLATE + "}";
	/**
	 * interact with the config of a template for a specific key
	 */
	public static final String CONFIG_TEMPLATE_KEY = "/{" + IRestPath.VAR_TEMPLATE + "}/{" + IRestPath.VAR_KEY + "}";
	/**
	 * interact with the config of a service of a template
	 */
	public static final String CONFIG_TEMPLATE_SERVICE = "/{" + IRestPath.VAR_TEMPLATE + "}/{" + IRestPath.VAR_SERVICE + "}";
	/**
	 * interact with the config of a service of a template for a specific key
	 */
	public static final String CONFIG_TEMPLATE_SERVICE_KEY = "/{" + IRestPath.VAR_TEMPLATE + "}/{" + IRestPath.VAR_SERVICE + "}/{" + IRestPath.VAR_KEY + "}";
	
	// -------------------------------------------------------
	// HOST
	// -------------------------------------------------------
	/**
	 * host api
	 */
	public static final String HOST = "/hosts";
	/**
	 * interact with the services of a host
	 */
	public static final String HOST_SERVICES = "/{" + IRestPath.VAR_HOST + "}/services";
	/**
	 * interact with the services of a host
	 */
	public static final String HOST_SERVICE_SVC = "/{" + IRestPath.VAR_HOST + "}/services/{" + IRestPath.VAR_SERVICE + "}";
	/**
	 * check if host is in sync with template
	 */
	public static final String HOST_SYNC = "/{" + IRestPath.VAR_HOST + "}/synced";
	/**
	 * starts a service on host
	 */
	public static final String HOST_SERVICE_START = IRestPath.HOST_SERVICE_SVC + "/start";
	/**
	 * stops a service on a host
	 */
	public static final String HOST_SERVICE_STOP = IRestPath.HOST_SERVICE_SVC + "/stop";
	/**
	 * restarts a service on a host
	 */
	public static final String HOST_SERVICE_RESTART = IRestPath.HOST_SERVICE_SVC + "/restart";
	
	// -------------------------------------------------------
	// PACKAGE
	// -------------------------------------------------------
	/**
	 * host api
	 */
	public static final String PKG = "/packages";
	/**
	 * interact with the package versions of a host
	 */
	public static final String PKG_VERSION = "/{" + IRestPath.VAR_PKG + "}/versions";
	/**
	 * interact with the package versions of a host
	 */
	public static final String PKG_VERSION_SINGLE = IRestPath.PKG_VERSION + "/{" + IRestPath.VAR_VERSION + "}";
	
	// -------------------------------------------------------
	// SERVICE
	// -------------------------------------------------------
	/**
	 * service api
	 */
	public static final String SERVICE = "/services";
	/**
	 * interact with the packages of a service
	 */
	public static final String SERVICE_PKG = "/{" + IRestPath.VAR_SERVICE + "}/packages";
	/**
	 * interact with the packages of a service
	 */
	public static final String SERVICE_PKG_SINGLE = IRestPath.SERVICE_PKG + "/{" + IRestPath.VAR_PKG + "}";
	
	// -------------------------------------------------------
	// SSH KEY
	// -------------------------------------------------------
	/**
	 * service api
	 */
	public static final String SSHKEY = "/sshkeys";
	
	// -------------------------------------------------------
	// TEMPLATE
	// -------------------------------------------------------
	/**
	 * template api
	 */
	public static final String TEMPLATE = "/templates";
	/**
	 * interact with the services of a template
	 */
	public static final String TEMPLATE_SERVICE = "/{" + IRestPath.VAR_TEMPLATE + "}/services";
	/**
	 * interact with the services of a template
	 */
	public static final String TEMPLATE_SERVICE_SINGLE = IRestPath.TEMPLATE_SERVICE + "/{" + IRestPath.VAR_SERVICE + "}";
	/**
	 * interact with the hosts of a template
	 */
	public static final String TEMPLATE_HOST = "/{" + IRestPath.VAR_TEMPLATE + "}/hosts";
	/**
	 * interact with the hosts of a template
	 */
	public static final String TEMPLATE_HOST_SINGLE = IRestPath.TEMPLATE_HOST + "/{" + IRestPath.VAR_HOST + "}";
	/**
	 * interact with the sshkeys of a template
	 */
	public static final String TEMPLATE_SSHKEY = "/{" + IRestPath.VAR_TEMPLATE + "}/sshkeys";
	/**
	 * interact with the sshkeys of a template
	 */
	public static final String TEMPLATE_SSHKEY_SINGLE = IRestPath.TEMPLATE_SSHKEY + "/{" + IRestPath.VAR_NAME + "}";
	/**
	 * interact with the package versions of a template
	 */
	public static final String TEMPLATE_VERSION = "/{" + IRestPath.VAR_TEMPLATE + "}/package/versions";
	/**
	 * interact with the package versions of a template
	 */
	public static final String TEMPLATE_VERSION_SINGLE = IRestPath.TEMPLATE_VERSION + "/{" + IRestPath.VAR_PKG + "}/{" + IRestPath.VAR_VERSION + "}";
	
	// -------------------------------------------------------
	// IOModule
	// -------------------------------------------------------
	/**
	 * IOModule api
	 */
	public static final String IO = "/io";
	/**
	 * io versions
	 */
	public static final String IO_VERSION = "/versions";
}
