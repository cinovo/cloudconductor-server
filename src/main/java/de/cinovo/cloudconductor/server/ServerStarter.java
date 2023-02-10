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
 * and limitations under the License. #L%
 */

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.LifecyclePhase;
import de.taimos.daemon.log4j.Log4jDaemonProperties;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.properties.FilePropertyProvider;
import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.daemon.spring.Configuration;
import de.taimos.dvalin.daemon.DvalinLifecycleAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Copyright 2012 Cinovo AG<br>
 * <br>
 * Starter program for the Config Server.
 *
 * @author mhilbert
 */
public class ServerStarter extends DvalinLifecycleAdapter {

	/**
	 * C2 properties file name
	 */
	public static final String CLOUDCONDUCTOR_PROPERTIES = "cloudconductor.properties";
	/**
	 * the name of the server daemon
	 */
	public static final String DAEMON_NAME = "cloudconductor";
	private static final Logger log = LoggerFactory.getLogger(ServerStarter.class);


	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		Log4jLoggingConfigurer.setup();
		if(ServerStarter.checkInstalled()) {
			DaemonStarter.startDaemon(ServerStarter.DAEMON_NAME, new ServerStarter());
		} else {
			ServerStarter.log.error("Properties file '" + ServerStarter.CLOUDCONDUCTOR_PROPERTIES + "' is missing!");
		}
	}

	private static boolean checkInstalled() {
		File f = new File(ServerStarter.CLOUDCONDUCTOR_PROPERTIES);
		return f.exists();
	}
	
	@Override
	protected void doBeforeSpringStart() {
		super.doBeforeSpringStart();
		
		String profiles = System.getProperty(Configuration.PROFILES, Configuration.PROFILES_PRODUCTION);
		if (!profiles.isEmpty()) {
			profiles += ",";
		}
		profiles += "http";
		System.setProperty(Configuration.PROFILES, profiles);
		
		this.logger.info("CloudConductor Version: {}", ServerStarter.class.getPackage().getImplementationVersion());
	}

	@Override
	public void exception(LifecyclePhase phase, Throwable exception) {
		if (ServerStarter.log.isErrorEnabled()) {
			ServerStarter.log.error("Exception in phase " + phase.name() + ": ", exception);
		}
	}

	@Override
	public IPropertyProvider getPropertyProvider() {
		return new FilePropertyProvider(ServerStarter.CLOUDCONDUCTOR_PROPERTIES);
	}

	@Override
	protected void loadBasicProperties(Map<String, String> map) {
		super.loadBasicProperties(map);
		map.put("ds.package", "de/cinovo/cloudconductor/server");
		map.put("jaxrs.path", "/api");
		map.put("jetty.sessions", "true");
		map.put(Log4jDaemonProperties.LOGGER_FILE, "true");
	}
}
