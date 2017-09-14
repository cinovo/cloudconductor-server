package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class WebSocketConfig {
	
	private String basePath;
	private long timeout;
	
	
	/**
	 * Creates an empty configuration for WebSockets.
	 */
	public WebSocketConfig() {
		
	}
	
	/**
	 * Creates a new configuration for WebSockets with a given base path.
	 * 
	 * @param basePath the base path to be set
	 * @param timeout the timeout in millis to set
	 */
	public WebSocketConfig(String basePath, long timeout) {
		this.basePath = basePath;
		this.setTimeout(timeout);
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return this.basePath;
	}
	
	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return this.timeout;
	}
	
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
}
