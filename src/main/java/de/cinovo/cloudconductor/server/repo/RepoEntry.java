package de.cinovo.cloudconductor.server.repo;

import java.util.Date;

public class RepoEntry {

	private String name;

	private long size;

	private boolean directory;

	private Date modified;


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
	
}
