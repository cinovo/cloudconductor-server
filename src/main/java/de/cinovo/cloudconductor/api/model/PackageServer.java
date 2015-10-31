package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.cinovo.cloudconductor.server.repo.indexer.RepoIndexerType;
import de.cinovo.cloudconductor.server.repo.provider.RepoProviderType;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class PackageServer {
	
	private Long id;
	private String serverGroup;
	private String path;
	private String description;
	
	private RepoIndexerType indexerType;
	private RepoProviderType providerType;
	
	// file provider & http provider infos
	private String basePath;
	
	// aws provider infos
	private String bucketName;
	private String awsRegion;
	private String accessKeyId;
	private String secretKey;
	
	
	/**
	 * @param id the id
	 * @param serverGroup the server group
	 * @param path the path
	 * @param description the description
	 * @param indexerType the indexer type
	 * @param providerType the provider type
	 */
	public PackageServer(@JsonProperty("id") Long id, @JsonProperty("serverGroup") String serverGroup, @JsonProperty("path") String path, @JsonProperty("description") String description, @JsonProperty("indexerType") RepoIndexerType indexerType, @JsonProperty("providerType") RepoProviderType providerType) {
		super();
		this.id = id;
		this.serverGroup = serverGroup;
		this.path = path;
		this.description = description;
		this.indexerType = indexerType;
		this.providerType = providerType;
	}
	
	/**
	 * @param id the id
	 * @param serverGroup the server group
	 * @param path the path
	 * @param description the description
	 * @param indexerType the indexer type
	 * @param providerType the provider type
	 * @param basePath the base type
	 * @param bucketName the bucket name
	 * @param awsRegion the aws region
	 * @param accessKeyId the access key
	 * @param secretKey the secret key
	 */
	public PackageServer(@JsonProperty("id") Long id, @JsonProperty("serverGroup") String serverGroup, @JsonProperty("path") String path, @JsonProperty("description") String description, @JsonProperty("indexerType") RepoIndexerType indexerType, @JsonProperty("providerType") RepoProviderType providerType, @JsonProperty("basePath") String basePath, @JsonProperty("bucketName") String bucketName, @JsonProperty("awsRegion") String awsRegion, @JsonProperty("accessKeyId") String accessKeyId, @JsonProperty("secretKey") String secretKey) {
		super();
		this.id = id;
		this.serverGroup = serverGroup;
		this.path = path;
		this.description = description;
		this.indexerType = indexerType;
		this.providerType = providerType;
		this.basePath = basePath;
		this.bucketName = bucketName;
		this.awsRegion = awsRegion;
		this.accessKeyId = accessKeyId;
		this.secretKey = secretKey;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the serverGroup
	 */
	public String getServerGroup() {
		return this.serverGroup;
	}
	
	/**
	 * @param serverGroup the serverGroup to set
	 */
	public void setServerGroup(String serverGroup) {
		this.serverGroup = serverGroup;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the indexerType
	 */
	public RepoIndexerType getIndexerType() {
		return this.indexerType;
	}
	
	/**
	 * @param indexerType the indexerType to set
	 */
	public void setIndexerType(RepoIndexerType indexerType) {
		this.indexerType = indexerType;
	}
	
	/**
	 * @return the providerType
	 */
	public RepoProviderType getProviderType() {
		return this.providerType;
	}
	
	/**
	 * @param providerType the providerType to set
	 */
	public void setProviderType(RepoProviderType providerType) {
		this.providerType = providerType;
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return this.basePath;
	}
	
	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return this.bucketName;
	}
	
	/**
	 * @param bucketName the bucketName to set
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	/**
	 * @return the awsRegion
	 */
	public String getAwsRegion() {
		return this.awsRegion;
	}
	
	/**
	 * @param awsRegion the awsRegion to set
	 */
	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}
	
	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return this.accessKeyId;
	}
	
	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}
	
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return this.secretKey;
	}
	
	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
}
