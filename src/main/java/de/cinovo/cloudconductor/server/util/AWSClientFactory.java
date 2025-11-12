package de.cinovo.cloudconductor.server.util;

import com.amazonaws.regions.Region;

import de.cinovo.cloudconductor.server.model.ERepoMirror;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.AwsClient;

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
	 * see factory comment
	 *
	 * @param <T> the class of the client to create
	 * @param clientClass the class of the client to create
	 * @return the created client
	 */
	public static <T extends AwsClient> T createClient(Class<T> clientClass) {
		String regionName = System.getProperty("aws.region", "eu-west-1");
		Region region = Region.getRegion(software.amazon.awssdk.regions.Region.of(regionName));
		
		return region.createClient(clientClass, null, null);
	}
	
	/**
	 * @param <T> the class of the client to create
	 * @param clientClass the class of the client to create
	 * @param mirror the mirror information to use
	 * @return the created client
	 */
	public static <T extends AwsClient> T createClient(Class<T> clientClass, ERepoMirror mirror) {
		String regionName = "eu-west-1";
		if ((mirror.getAwsRegion() != null) && !mirror.getAwsRegion().isEmpty()) {
			regionName = mirror.getAwsRegion();
		}
		Region region = Region.getRegion(software.amazon.awssdk.regions.Region.of(regionName));
		
		AwsCredentialsProvider credentialProvider = new PackageCredentialProvider(mirror);
		return region.createClient(clientClass, credentialProvider, null);
	}
	
}
