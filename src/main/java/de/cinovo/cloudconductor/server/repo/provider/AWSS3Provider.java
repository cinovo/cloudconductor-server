package de.cinovo.cloudconductor.server.repo.provider;

import com.amazonaws.services.s3.model.*;
import de.cinovo.cloudconductor.api.enums.RepoProviderType;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.util.AWSClientFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2014 Hoegernet<br>
 *
 * @author Thorsten Hoeger
 */
public class AWSS3Provider implements IRepoProvider {
	
	private final ERepo repo;
	private ERepoMirror mirror;
	private S3Client s3Client;
	
	
	/**
	 * @param mirror the mirror to contact
	 * @param repo the repo of the mirror
	 */
	public AWSS3Provider(ERepoMirror mirror, ERepo repo) {
		this.repo = repo;
		if (mirror.getProviderType() == RepoProviderType.AWSS3) {
			this.mirror = mirror;
			this.s3Client = AWSClientFactory.createClient(S3Client.class, mirror);
		}
	}
	
	@Override
	public boolean isListable() {
		return true;
	}
	
	@Override
	public List<RepoEntry> getEntries(String folder) {
		List<RepoEntry> res = new ArrayList<>();
		if ((this.mirror == null) || (this.s3Client == null)) {
			return res;
		}
		Set<String> folderNames = new HashSet<>();
		
		ListObjectsResponse objects = this.s3Client.listObjects(ListObjectsRequest.builder().bucket(this.mirror.getBucketName()).prefix(folder)
			.build());
		List<S3Object> summaries = objects.objectSummaries();
		for (S3Object objectSummary : summaries) {
			String file = objectSummary.key().substring(folder.length());
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
				fil.setModified(objectSummary.lastModified());
				fil.setSize(objectSummary.size());
				fil.setChecksum(objectSummary.eTag());
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
		if ((this.mirror == null) || (this.s3Client == null)) {
			return null;
		}
		final HeadObjectResponse obj = this.s3Client.headObject(HeadObjectRequest.builder().bucket(this.mirror.getBucketName()).key(key)
			.build());
		RepoEntry fil = new RepoEntry();
		fil.setName(key);
		fil.setDirectory(false);
		fil.setModified(/*AWS SDK for Java v2 migration: Transform for ObjectMetadata setter - lastModified - is not supported, please manually migrate the code by setting it on the v2 request/response object.*/obj.lastModified());
		fil.setSize();
		fil.setContentType();
		fil.setChecksum(/*AWS SDK for Java v2 migration: Transform for ObjectMetadata setter - eTag - is not supported, please manually migrate the code by setting it on the v2 request/response object.*/obj.eTag());
		return fil;
	}
	
	@Override
	public InputStream getEntryStream(String key) {
		if ((this.mirror == null) || (this.s3Client == null)) {
			return null;
		}
		ResponseInputStream<GetObjectResponse> s3Object = this.s3Client.getObject(GetObjectRequest.builder().bucket(this.mirror.getBucketName()).key(key)
			.build());
		return s3Object;
	}
	
	@Override
	public String getRepoName() {
		if ((this.repo == null)) {
			return null;
		}
		return this.repo.getName();
	}
	
}
