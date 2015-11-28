package de.cinovo.cloudconductor.server.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import de.cinovo.cloudconductor.server.model.EPackageServer;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageCredentialProvider implements AWSCredentialsProvider {
	
	private BasicAWSCredentials cred;
	
	
	/**
	 * @param ps the packageserver to use
	 */
	public PackageCredentialProvider(EPackageServer ps) {
		this.cred = new BasicAWSCredentials(ps.getAccessKeyId(), ps.getSecretKey());
	}
	
	@Override
	public AWSCredentials getCredentials() {
		return this.cred;
	}
	
	@Override
	public void refresh() {
		// nothing to do;
	}
	
}
