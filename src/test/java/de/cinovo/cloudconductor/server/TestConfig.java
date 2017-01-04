package de.cinovo.cloudconductor.server;

import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfig;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
public class TestConfig extends RunnerConfig {
	
	/**
	 * 
	 */
	public TestConfig() {
		this.addProperty("ds.type", "HSQL");
		this.addProperty("ds.demodata", "true");
		this.addProperty("ds.demofile", "liquibase/cf.sql");
		this.addProperty("svc.port", String.valueOf(RunnerConfig.randomPort()));
		this.addProperty("cloudconductor.username", "admin");
		this.addProperty("cloudconductor.password", "password");
		this.addProperty("cloudconductor.url", "localhost:8098");
		this.addProperty("hazelcast.members", "localhost");
		this.addProperty("ds.package", "de/cinovo/cloudconductor/server/model");
		this.addProperty("jaxrs.path", "/api");
		this.addProperty("jetty.sessions", "true");
		this.addProperty("cloudconductor.restauthmandatory", "false");
	}
	
	@Override
	public String getSpringFile() {
		return "spring/beans.xml";
	}
	
	@Override
	public String getServicePackage() {
		return "de.cinovo.cloudconductor.server";
	}
	
}
