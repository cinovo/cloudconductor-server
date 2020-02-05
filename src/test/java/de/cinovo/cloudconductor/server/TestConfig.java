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
		this.addProperty("cloudconductor.url", "localhost:8098");
		this.addProperty("ds.package", "de/cinovo/cloudconductor/server/model");
		this.addProperty("jaxrs.path", "/api");
		this.addProperty("jetty.sessions", "true");
		this.addProperty("jwtauth.issuer", "cloudconductor");
		this.addProperty("jwtauth.secret", "4ED267FE5BBA826F6D2EE71A3A5EE491275051234F997B19068FECF9729FC2AC");
		this.addProperty("indexTaskType", "none");
	}
	
	@Override
	public String getSpringFile() {
		return "spring/dvalin.xml";
	}

	@Override
	public String getServicePackage() {
		return "de.cinovo.cloudconductor.server";
	}
	
}
