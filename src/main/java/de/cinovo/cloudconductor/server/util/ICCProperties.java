package de.cinovo.cloudconductor.server.util;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface ICCProperties {
	
	/**
	 * the db type
	 */
	String DB_TYPE = "ds.type";
	/**
	 * the db host
	 */
	String DB_HOST = "ds.host";
	/**
	 * the db port
	 */
	String DB_PORT = "ds.port";
	/**
	 * the db username
	 */
	String DB_USER = "ds.username";
	/**
	 * the db password
	 */
	String DB_PW = "ds.pw";
	/**
	 * the db name
	 */
	String DB_NAME = "ds.dbname";
	
	/**
	 * the cloud conductor port
	 */
	String CC_PORT = "svc.port";
	/**
	 * the cloud conductor name
	 */
	String CC_NAME = "cloudconductor.name";
	/**
	 * the cloud conductor user
	 */
	String CC_USER = "cloudconductor.username";
	/**
	 * the cloud conductor password
	 */
	String CC_PW = "cloudconductor.password";
}
