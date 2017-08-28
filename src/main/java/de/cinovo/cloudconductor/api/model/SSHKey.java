package de.cinovo.cloudconductor.api.model;

import java.util.Date;
import java.util.List;

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
	private String username;
	private String key;
	private Date lastChanged;
	private List<String> templates;
	
	
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
	 * @param owner the owner of the ssh key
	 * @param key the content of the ssh key
	 * @param lastChangedDate the timestamp of the last change
	 */
	public SSHKey(String owner, String key, Long lastChangedDate) {
		this.owner = owner;
		this.key = key;
		
		this.lastChanged = new Date(lastChangedDate);
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
	 * @return the user name for the ssh key
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param username the user name to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	
	/**
	 * @return set of template names this ssh key belongs to
	 */
	public List<String> getTemplates() {
		return this.templates;
	}
	
	/**
	 * @param templates the template names to set
	 */
	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}
	
}
