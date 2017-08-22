package de.cinovo.cloudconductor.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class SSHKey {
	
	private String owner;
	private String key;
	private Date lastChanged;
	
	
	/**
	 * Create a new ssh key.
	 */
	public SSHKey() {
		
	}
	
	/**
	 * 
	 * @param owner the name of the key owner
	 * @param key the content of the key
	 */
	public SSHKey(String owner, String key) {
		this.owner = owner;
		this.key = key;
		this.lastChanged = new Date();
	}
	
	/**
	 * @return owner of the ssh key
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @param owner the name of the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the content of the ssh key
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * @return the last date when this key was created or changed
	 */
	public Date getLastChanged() {
		return this.lastChanged;
	}
	
	/**
	 * @param lastChanged the change date to set
	 */
	public void setLastChanged(Date lastChanged) {
		this.lastChanged = lastChanged;
	}
	
}
