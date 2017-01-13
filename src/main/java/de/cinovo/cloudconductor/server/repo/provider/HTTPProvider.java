package de.cinovo.cloudconductor.server.repo.provider;

import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.taimos.httputils.WS;
import de.taimos.httputils.WSConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class HTTPProvider implements IRepoProvider {
	
	private EPackageServer packageServer;
	
	
	/**
	 * @param packageServer the package server to contact
	 */
	public HTTPProvider(EPackageServer packageServer) {
		if (packageServer.getProviderType() == RepoProviderType.AWSS3) {
			this.packageServer = packageServer;
		}
	}
	
	@Override
	public boolean isListable() {
		return false;
	}
	
	@Override
	public List<RepoEntry> getEntries(String folder) {
		throw new UnsupportedOperationException("This provider does not support listing");
	}
	
	@Override
	public RepoEntry getEntry(String key) {
		HttpResponse response = WS.url(this.packageServer.getBasePath() + key).get();
		RepoEntry e = new RepoEntry();
		e.setDirectory(false);
		e.setName(key.substring(Math.max(0, key.lastIndexOf("/") + 1)));
		e.setSize(this.getSize(response));
		e.setModified(new Date());
		e.setChecksum(this.getChecksum(response));
		e.setContentType(this.getType(response));
		return e;
	}
	
	private String getType(HttpResponse response) {
		Header header = response.getFirstHeader(WSConstants.HEADER_CONTENT_TYPE);
		if (header != null) {
			return header.getValue();
		}
		return MediaType.APPLICATION_OCTET_STREAM;
	}
	
	private String getChecksum(HttpResponse response) {
		Header header = response.getFirstHeader(WSConstants.HEADER_CONTENT_MD5);
		if (header != null) {
			return header.getValue();
		}
		return null;
	}
	
	private long getSize(HttpResponse response) {
		Header sizeHeader = response.getFirstHeader(WSConstants.HEADER_CONTENT_LENGTH);
		if (sizeHeader != null) {
			String size = sizeHeader.getValue();
			if ((size != null) && size.matches("[0-9]+")) {
				return Long.valueOf(size);
			}
		}
		return 0;
	}
	
	@Override
	public InputStream getEntryStream(String key) {
		HttpResponse response = WS.url(this.packageServer.getBasePath() + key).get();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			try {
				return entity.getContent();
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException("Failed to createEntity stream", e);
			}
		}
		throw new RuntimeException("HTTP entity was null");
	}
	
	@Override
	public String getPackageServerGroupName() {
		return this.packageServer.getServerGroup().getName();
	}
}
