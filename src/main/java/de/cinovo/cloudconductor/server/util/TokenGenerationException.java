package de.cinovo.cloudconductor.server.util;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public class TokenGenerationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public TokenGenerationException() {
		// --
	}
	
	/**
	 * @param msg the message of the exception
	 */
	public TokenGenerationException(String msg) {
		super(msg);
	}
}
