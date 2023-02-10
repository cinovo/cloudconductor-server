package de.cinovo.cloudconductor.server;

import de.taimos.daemon.spring.Configuration;
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
		
		// https://ossindex.sonatype.org/vulnerability/CVE-2022-41853?component-type=maven&component-name=org.hsqldb%2Fhsqldb&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
		this.addProperty("hsqldb.method_class_names", "abc"); // TODO remove as soon as hsqldb can be updated
	}
	
	@Override
	protected void addProperty(String key, String value) {
		// TODO remove this override as soon as dvalin SpringDaemonTestRunner allows additional spring profiles for tests
		if (Configuration.PROFILES.equals(key)) {
			if (!value.isEmpty()) {
				value += ",";
			}
			value += "http";
		}
		System.setProperty(key, value);
		super.addProperty(key, value);
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
