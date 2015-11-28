package de.cinovo.cloudconductor.server.model;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.cinovo.cloudconductor.server.repo.indexer.RepoIndexerType;
import de.cinovo.cloudconductor.server.repo.provider.RepoProviderType;
import de.taimos.dao.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "packageserver", schema = "cloudconductor")
public class EPackageServer implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private EPackageServerGroup serverGroup;
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
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the path
	 */
	@Column(name = "path")
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EPackageServer)) {
			return false;
		}
		EPackageServer other = (EPackageServer) obj;
		
		if (this.getPath().equals(other.getPath()) && this.id.equals(other.getId())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int val = (this.getPath() == null) ? 0 : this.getPath().hashCode();
		int idVal = (this.getId() == null) ? 0 : this.getId().hashCode();
		return val * idVal;
	}
	
	/**
	 * @return the serverGroup
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "servergroupid")
	public EPackageServerGroup getServerGroup() {
		return this.serverGroup;
	}
	
	/**
	 * @param serverGroup the serverGroup to set
	 */
	public void setServerGroup(EPackageServerGroup serverGroup) {
		this.serverGroup = serverGroup;
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
}
