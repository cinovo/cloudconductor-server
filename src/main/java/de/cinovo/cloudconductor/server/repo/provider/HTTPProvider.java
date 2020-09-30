package de.cinovo.cloudconductor.server.repo.provider;

import de.cinovo.cloudconductor.api.enums.RepoProviderType;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.taimos.httputils.HTTPResponse;
import de.taimos.httputils.WS;
import de.taimos.httputils.WSConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public class HTTPProvider implements IRepoProvider {
	
	private ERepoMirror mirror;
	
	
	/**
	 * @param mirror the mirror to contact
	 */
	public HTTPProvider(ERepoMirror mirror) {
		if (mirror.getProviderType() == RepoProviderType.HTTP) {
			this.mirror = mirror;
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
		if ((this.mirror == null) || (this.mirror.getBasePath() == null)) {
			return null;
		}

		try (HTTPResponse response = WS.url(this.getUrl(key)).get()) {
			RepoEntry e = new RepoEntry();
			e.setDirectory(false);
			e.setName(key.substring(Math.max(0, key.lastIndexOf("/") + 1)));
			e.setSize(this.getSize(response));
			e.setModified(new Date());
			e.setChecksum(this.getChecksum(response));
			e.setContentType(this.getType(response));
			return e;
		}
	}
	
	private String getUrl(String key) {
		return UriBuilder.fromPath(this.mirror.getBasePath()).path(key).build().toString();
	}
	
	private String getType(HTTPResponse response) {
		HttpResponse r = response.getResponse();
		Header header = r.getFirstHeader(WSConstants.HEADER_CONTENT_TYPE);
		if (header != null) {
			return header.getValue();
		}
		return MediaType.APPLICATION_OCTET_STREAM;
	}
	
	private String getChecksum(HTTPResponse response) {
		HttpResponse r = response.getResponse();
		Header header = response.getResponse().getFirstHeader(WSConstants.HEADER_CONTENT_MD5);
		if (header != null) {
			return header.getValue();
		}
		if (r.getEntity() != null) {
			String checksum = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				// Using MessageDigest update() method to provide input
				byte[] buffer = new byte[8192];
				int numOfBytesRead;
				while ((numOfBytesRead = r.getEntity().getContent().read(buffer)) > 0) {
					md.update(buffer, 0, numOfBytesRead);
				}
				byte[] hash = md.digest();
				checksum = new BigInteger(1, hash).toString(16); // don't use this, truncates leading zero
			} catch (Exception ex) {
				// do nothing
			}
			return checksum;
		}
		return null;
	}
	
	private long getSize(HTTPResponse response) {
		Header sizeHeader = response.getResponse().getFirstHeader(WSConstants.HEADER_CONTENT_LENGTH);
		if (sizeHeader != null) {
			String size = sizeHeader.getValue();
			if ((size != null) && size.matches("[0-9]+")) {
				return Long.parseLong(size);
			}
		}
		return 0;
	}
	
	@Override
	public InputStream getEntryStream(String relativeFilePath) {
		String url = this.getUrl(relativeFilePath);
		HTTPResponse response = WS.url(url).get();
		HttpEntity entity = response.getResponse().getEntity();
		if (entity == null) {
			throw new RuntimeException(String.format("No HTTP entity in Response for '%s'", url));
		}

		try {
			return entity.getContent();
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException(String.format("Failed to create entity stream for response of '%s': ", url), e);
		}
	}
	
	@Override
	public String getRepoName() {
		return this.mirror.getRepo().getName();
	}
}
