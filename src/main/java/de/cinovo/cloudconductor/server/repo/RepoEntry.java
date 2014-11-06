package de.cinovo.cloudconductor.server.repo;

import java.util.Date;

import javax.ws.rs.core.MediaType;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class RepoEntry {

	private String name;

	private long size;

	private boolean directory;

	private Date modified;

	private String contentType = MediaType.APPLICATION_OCTET_STREAM;

	private String checksum;


	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * @param size the size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return is directory
	 */
	public boolean isDirectory() {
		return this.directory;
	}

	/**
	 * @param directory if it's an directory
	 */
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	
	/**
	 * @return modification date
	 */
	public Date getModified() {
		return this.modified;
	}
	
	/**
	 * @param modified modification date
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * @return the content type
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * @param contentType the content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return this.checksum;
	}
	
	/**
	 * @param checksum the checksum
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	/**
	 * @param other another repo
	 * @return whether a change occurred or not
	 */
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
