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

import de.cinovo.cloudconductor.server.tasks.ServerTaskHelper;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.LifecyclePhase;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.properties.FilePropertyProvider;
import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.dvalin.daemon.DvalinLifecycleAdapter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
	
	// Exception messages.
	private static final String EXCEPTION_IN_PHASE = "Exception in phase %s.";
	/**
	 * the logger
	 */
	private static final Logger log = Logger.getLogger(ServerStarter.class);
	
	
	/**
	 * Main method.
	 *
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		System.getProperties().put("jaxrs.path", "/api");
		Log4jLoggingConfigurer.setup();
		if (ServerStarter.checkInstalled()) {
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
		// In dev mode all classes related to the config server should log on the level DEBUG.
		if (DaemonStarter.isDevelopmentMode()) {
			Logger.getLogger("de.cinovo.cloudconductor.server").setLevel(Level.DEBUG);
		}
		super.doBeforeSpringStart();
	}
	
	@Override
	protected void doAfterSpringStart() {
		ServerTaskHelper initializer = this.getContext().getBean(ServerTaskHelper.class);
		initializer.init();
		super.doAfterSpringStart();
	}
	
	@Override
	public void exception(LifecyclePhase phase, Throwable exception) {
		ServerStarter.log.error(String.format(ServerStarter.EXCEPTION_IN_PHASE, phase.name()), exception);
	}
	
	@Override
	public IPropertyProvider getPropertyProvider() {
		return new FilePropertyProvider(ServerStarter.CLOUDCONDUCTOR_PROPERTIES);
	}
	
	@Override
	protected void loadBasicProperties(Map<String, String> map) {
		super.loadBasicProperties(map);
		map.put("ds.package", "de/cinovo/cloudconductor/server/");
		map.put("jaxrs.path", "/api");
		map.put("jetty.sessions", "true");
	}
}
