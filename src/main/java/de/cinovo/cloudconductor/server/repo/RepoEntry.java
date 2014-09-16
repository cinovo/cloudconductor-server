package de.cinovo.cloudconductor.server.repo;

import java.util.Date;

import javax.ws.rs.core.MediaType;

public class RepoEntry {
	
	private String name;
	
	private long size;
	
	private boolean directory;
	
	private Date modified;
	
	private String contentType = MediaType.APPLICATION_OCTET_STREAM;
	
	
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
	
}
