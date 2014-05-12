package de.cinovo.cloudconductor.server.web.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class AjaxAnswer {
	
	/**
	 * Copyright 2014 Cinovo AG<br>
	 * <br>
	 * 
	 * @author psigloch
	 * 
	 */
	public enum AjaxAnswerType {
		/** refresh the page */
		REFRESH,
		/** get page via get */
		GET,
		/** get page via post */
		POST
	}
	
	
	private AjaxAnswerType type;
	private String path;
	private String info;
	
	
	/**
	 * @param path the redirect path
	 */
	public AjaxAnswer(String path) {
		this.type = AjaxAnswerType.REFRESH;
		this.path = path;
	}
	
	/**
	 * @param path the redirect path
	 * @param filter the filter
	 */
	public AjaxAnswer(String path, String filter) {
		this.type = AjaxAnswerType.REFRESH;
		this.path = path + "?filter=" + filter;
	}
	
	/**
	 * @param path the redirect path
	 * @param type AjaxRedirectType
	 */
	public AjaxAnswer(String path, AjaxAnswerType type) {
		this.type = type;
		this.path = path;
	}
	
	/**
	 * @param path the redirect path
	 * @param filter the filter
	 * @param type {@link AjaxAnswerType}
	 */
	public AjaxAnswer(String path, String filter, AjaxAnswerType type) {
		this.type = type;
		this.path = path + "?filter=" + filter;
	}
	
	/**
	 * @return the type
	 */
	public AjaxAnswerType getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(AjaxAnswerType type) {
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
