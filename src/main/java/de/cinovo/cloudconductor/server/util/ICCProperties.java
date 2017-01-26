package de.cinovo.cloudconductor.server.util;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public interface ICCProperties {

	/** the db type */
	public static final String DB_TYPE = "ds.type";
	/** the db host */
	public static final String DB_HOST = "ds.host";
	/** the db port */
	public static final String DB_PORT = "ds.port";
	/** the db username */
	public static final String DB_USER = "ds.username";
	/** the db password */
	public static final String DB_PW = "ds.pw";
	/** the db name */
	public static final String DB_NAME = "ds.dbname";

	/** the cloud conductor port */
	public static final String CC_PORT = "svc.port";
	/** the cloud conductor name */
	public static final String CC_NAME = "cloudconductor.name";
	/** the cloud conductor user */
	public static final String CC_USER = "cloudconductor.username";
	/** the cloud conductor password */
	public static final String CC_PW = "cloudconductor.password";
}
