package de.cinovo.cloudconductor.server.websockets.model;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
public class WSHeartbeat {
	
	private String data;
	
	
	/**
	 * @return the data
	 */
	public String getData() {
		return this.data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
}
