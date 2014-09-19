package de.cinovo.cloudconductor.server.repo;

import java.util.Date;

import javax.ws.rs.core.MediaType;

public class RepoEntry {

	private String name;

	private long size;

	private boolean directory;

	private Date modified;

	private String contentType = MediaType.APPLICATION_OCTET_STREAM;

	private String checksum;


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isDirectory() {
		return this.directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	
	public Date getModified() {
		return this.modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getChecksum() {
		return this.checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public boolean hasChanged(RepoEntry other) {
		if (other == null) {
			return true;
		}
		if ((this.getChecksum() != null) && this.getChecksum().equals(other.getChecksum())) {
			return false;
		}
		if ((this.getChecksum() == null) && (this.getModified().equals(other.getModified())) && (this.getSize() == other.getSize())) {
			return false;
		}
		return true;
	}
	
}
