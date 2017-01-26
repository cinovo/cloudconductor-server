package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.cinovo.cloudconductor.server.repo.indexer.RepoIndexerType;
import de.cinovo.cloudconductor.server.repo.provider.RepoProviderType;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class RepoMirror {

	private Long id;
	private String repo;
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
	 * @return the repo
	 */
	public String getRepo() {
		return this.repo;
	}

	/**
	 * @param repo the repo to set
	 */
	public void setRepo(String repo) {
		this.repo = repo;
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
