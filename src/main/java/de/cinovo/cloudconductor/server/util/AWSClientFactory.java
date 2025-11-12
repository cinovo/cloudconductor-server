package de.cinovo.cloudconductor.server.util;


import de.cinovo.cloudconductor.server.model.ERepoMirror;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 * Factory to create AWS Clients<br>
 * <br>
 * The default Region is <i>eu-west-1</i>. You can use another region by setting the SystemProperty "aws.region"<br>
 * <br>
 * It uses a provider chain that looks for credentials in this order:
 * <ul>
 * <li>Environment variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY</li>
 * <li>Java System Properties - aws.accessKeyId and aws.secretKey</li>
 * <li>Profile config file</li>
 * <li>Instance profile credentials delivered through the Amazon EC2 metadata service</li>
 * </ul>
 *
 * @author Thorsten Hoeger
 *
 */
public final class AWSClientFactory {
	
	private AWSClientFactory() {
		// private utility class constructor
	}
	
	/**
	 * @param mirror the mirror information to use
	 * @return the created client
	 */
	public static S3Client createS3Client(ERepoMirror mirror) {
		String regionName = "eu-west-1";
		if ((mirror.getAwsRegion() != null) && !mirror.getAwsRegion().isEmpty()) {
			regionName = mirror.getAwsRegion();
		}
		Region region = Region.of(regionName);
		
		AwsCredentialsProvider credentialProvider = new PackageCredentialProvider(mirror);
		return S3Client.builder().credentialsProvider(credentialProvider).region(region).build();
	}
	
}
