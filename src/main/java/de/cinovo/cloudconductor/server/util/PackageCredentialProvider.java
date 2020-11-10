package de.cinovo.cloudconductor.server.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import de.cinovo.cloudconductor.server.model.ERepoMirror;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageCredentialProvider implements AWSCredentialsProvider {
	
	private final BasicAWSCredentials cred;
	
	
	/**
	 * @param mirror the mirror to use
	 */
	public PackageCredentialProvider(ERepoMirror mirror) {
		this.cred = new BasicAWSCredentials(mirror.getAccessKeyId(), mirror.getSecretKey());
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
