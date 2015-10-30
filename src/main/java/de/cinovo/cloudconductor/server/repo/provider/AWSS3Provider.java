package de.cinovo.cloudconductor.server.repo.provider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.util.AWSClientFactory;

/**
 * Copyright 2014 Hoegernet<br>
 *
 * @author Thorsten Hoeger
 */
public class AWSS3Provider implements IRepoProvider {
	
	private EPackageServer packageServer;
	private AmazonS3 s3Client;
	
	
	/**
	 * @param packageServer the package server to contact
	 */
	public AWSS3Provider(EPackageServer packageServer) {
		if (packageServer.getProviderType() == RepoProviderType.AWSS3) {
			this.packageServer = packageServer;
			this.s3Client = AWSClientFactory.createClient(AmazonS3Client.class, packageServer);
		}
	}
	
	@Override
	public boolean isListable() {
		return true;
	}
	
	@Override
	public List<RepoEntry> getEntries(String folder) {
		List<RepoEntry> res = new ArrayList<>();
		if ((this.packageServer == null) || (this.s3Client == null)) {
			return res;
		}
		Set<String> folderNames = new HashSet<>();
		
		ObjectListing objects = this.s3Client.listObjects(this.packageServer.getBucketName(), folder);
		List<S3ObjectSummary> summaries = objects.getObjectSummaries();
		for (S3ObjectSummary objectSummary : summaries) {
			String file = objectSummary.getKey().substring(folder.length());
			if (file.contains("/")) {
				file = file.substring(0, file.indexOf("/"));
				if (!folderNames.contains(file)) {
					folderNames.add(file);
					RepoEntry dir = new RepoEntry();
					dir.setName(file);
					dir.setDirectory(true);
					res.add(dir);
				}
			} else {
				RepoEntry fil = new RepoEntry();
				fil.setName(file);
				fil.setDirectory(false);
				fil.setModified(objectSummary.getLastModified());
				fil.setSize(objectSummary.getSize());
				fil.setChecksum(objectSummary.getETag());
				res.add(fil);
			}
		}
		if (objects.isTruncated()) {
			// TODO handle this
		}
		return res;
	}
	
	@Override
	public RepoEntry getEntry(String key) {
		if ((this.packageServer == null) || (this.s3Client == null)) {
			return null;
		}
		final ObjectMetadata obj = this.s3Client.getObjectMetadata(this.packageServer.getBucketName(), key);
		RepoEntry fil = new RepoEntry();
		fil.setName(key);
		fil.setDirectory(false);
		fil.setModified(obj.getLastModified());
		fil.setSize(obj.getContentLength());
		fil.setContentType(obj.getContentType());
		fil.setChecksum(obj.getETag());
		return fil;
	}
	
	@Override
	public InputStream getEntryStream(String key) {
		if ((this.packageServer == null) || (this.s3Client == null)) {
			return null;
		}
		S3Object s3Object = this.s3Client.getObject(this.packageServer.getBucketName(), key);
		return s3Object.getObjectContent();
	}
	
	@Override
	public String getPackageServerGroupName() {
		if ((this.packageServer == null)) {
			return null;
		}
		return this.packageServer.getServerGroup().getName();
	}
	
}
