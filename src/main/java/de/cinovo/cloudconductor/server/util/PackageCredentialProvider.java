package de.cinovo.cloudconductor.server.util;

import de.cinovo.cloudconductor.server.model.ERepoMirror;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageCredentialProvider implements AwsCredentialsProvider {
	
	private final AwsBasicCredentials cred;
	
	
	/**
	 * @param mirror the mirror to use
	 */
	public PackageCredentialProvider(ERepoMirror mirror) {
		this.cred = AwsBasicCredentials.create(mirror.getAccessKeyId(), mirror.getSecretKey());
	}
	
	@Override
	public AwsCredentials getCredentials() {
		return this.cred;
	}
	
	@Override
	public void refresh() {
		// nothing to do;
	}
	
}
