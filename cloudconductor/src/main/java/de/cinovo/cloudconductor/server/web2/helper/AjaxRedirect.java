package de.cinovo.cloudconductor.server.web2.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class AjaxRedirect {
	
	public enum AjaxRedirectType {
		REFRESH, GET, POST
	}
	
	
	private AjaxRedirectType type;
	private String path;
	private String info;
	
	
	/**
	 * @param path the redirect path
	 */
	public AjaxRedirect(String path) {
		this.type = AjaxRedirectType.REFRESH;
		this.path = path;
	}
	
	/**
	 * @param path the redirect path
	 * @param filter the filter
	 */
	public AjaxRedirect(String path, String filter) {
		this.type = AjaxRedirectType.REFRESH;
		this.path = path + "?filter=" + filter;
	}
	
	/**
	 * @param path the redirect path
	 * @param type AjaxRedirectType
	 */
	public AjaxRedirect(String path, AjaxRedirectType type) {
		this.type = type;
		this.path = path;
	}
	
	/**
	 * @param path the redirect path
	 * @param filter the filter
	 */
	public AjaxRedirect(String path, String filter, AjaxRedirectType type) {
		this.type = type;
		this.path = path + "?filter=" + filter;
	}
	
	/**
	 * @return the type
	 */
	public AjaxRedirectType getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(AjaxRedirectType type) {
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
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return this.info;
	}
	
	/**
	 * @param info the info
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
}
