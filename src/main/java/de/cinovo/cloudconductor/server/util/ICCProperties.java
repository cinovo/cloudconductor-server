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

	/** do the repo index scan */
	public static final String REPO_SCAN = "repo.indexscan";
	/** the repo indexer */
	public static final String REPO_INDEXER = "repo.indexer";

	/** the repo provider */
	public static final String REPO_PROVIDER = "repo.provider";
	/** the repo basedir */
	public static final String REPO_BASEDIR = "repo.basedir";
	/** the repo baseurl */
	public static final String REPO_BASEURL = "repo.baseurl";
	/** the repo aws bucket */
	public static final String REPO_AWS_BUCKET = "repo.bucket";
	/** the repo aws access key id */
	public static final String REPO_AWS_ACCESS_KEY = "aws.accessKeyId";
	/** the repo aws secret key */
	public static final String REPO_AWS_SECRET_KEY = "aws.secretKey";
	
}
