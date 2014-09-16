package de.cinovo.cloudconductor.server.repo.provider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

/**
 * Copyright 2014 Hoegernet<br>
 *
 * @author Thorsten Hoeger
 */
public class AWSS3Provider implements IRepoProvider {
	
	@Autowired
	private AmazonS3 s3Client;
	
	@Value("${repo.bucket}")
	private String bucketName;
	
	private String indexETag;
	
	
	@Override
	public List<RepoEntry> getEntries(String folder) {
		List<RepoEntry> res = new ArrayList<>();
		Set<String> folderNames = new HashSet<>();
		
		ObjectListing objects = this.s3Client.listObjects(this.bucketName, folder);
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
		final ObjectMetadata obj = this.s3Client.getObjectMetadata(this.bucketName, key);
		RepoEntry fil = new RepoEntry();
		fil.setName(key);
		fil.setDirectory(false);
		fil.setModified(obj.getLastModified());
		fil.setSize(obj.getContentLength());
		fil.setContentType(obj.getContentType());
		return fil;
	}
	
	@Override
	public InputStream getEntryStream(String key) {
		S3Object s3Object = this.s3Client.getObject(this.bucketName, key);
		return s3Object.getObjectContent();
	}
	
	@Override
	public String getLatestIndex() {
		final ObjectMetadata obj = this.s3Client.getObjectMetadata(this.bucketName, IRepoProvider.INDEX_FILE);
		String eTag = obj.getETag();
		if ((this.indexETag != null) && this.indexETag.equals(eTag)) {
			return null;
		}
		this.indexETag = eTag;
		S3Object s3Object = this.s3Client.getObject(this.bucketName, IRepoProvider.INDEX_FILE);
		try {
			return StreamUtils.copyToString(s3Object.getObjectContent(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read index", e);
		}
	}

}
