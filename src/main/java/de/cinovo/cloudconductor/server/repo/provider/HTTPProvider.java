package de.cinovo.cloudconductor.server.repo.provider;

import de.cinovo.cloudconductor.api.enums.RepoProviderType;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.taimos.httputils.WS;
import de.taimos.httputils.WSConstants;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import javax.ws.rs.core.MediaType;
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
 *
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
		if(this.mirror != null && this.mirror.getBasePath() != null) {
			HttpResponse response = WS.url(this.mirror.getBasePath() + key).get();
			RepoEntry e = new RepoEntry();
			e.setDirectory(false);
			e.setName(key.substring(Math.max(0, key.lastIndexOf("/") + 1)));
			e.setSize(this.getSize(response));
			e.setModified(new Date());
			e.setChecksum(this.getChecksum(response));
			e.setContentType(this.getType(response));
			return e;
		}
		return null;
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
		if(response.getEntity() != null ) {
			String checksum = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				//Using MessageDigest update() method to provide input
				byte[] buffer = new byte[8192];
				int numOfBytesRead;
				while( (numOfBytesRead = response.getEntity().getContent().read(buffer)) > 0){
					md.update(buffer, 0, numOfBytesRead);
				}
				byte[] hash = md.digest();
				checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
			} catch (Exception ex) {
				//do nothing
			}
			return checksum;
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
		HttpResponse response = WS.url(this.mirror.getBasePath() + key).get();
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
	public String getRepoName() {
		return this.mirror.getRepo().getName();
	}
}
