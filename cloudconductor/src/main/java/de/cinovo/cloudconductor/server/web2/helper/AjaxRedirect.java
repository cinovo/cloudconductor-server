package de.cinovo.cloudconductor.server.web2.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class AjaxRedirect {
	
	private static final String REDIRECT = "REDIRECT";
	private String type;
	private String path;
	
	
	/**
	 * @param path the redirect path
	 */
	public AjaxRedirect(String path) {
		this.type = AjaxRedirect.REDIRECT;
		this.path = path;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
