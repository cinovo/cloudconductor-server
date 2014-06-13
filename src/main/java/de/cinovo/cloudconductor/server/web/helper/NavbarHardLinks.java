package de.cinovo.cloudconductor.server.web.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public enum NavbarHardLinks {
	
	/** hardlink for the configuration navbar element */
	config("Configuration"),
	/** hardlink for the options navbar element */
	options("Options"),
	/** hardlink for the links navbar element */
	links("Links");
	
	private String viewName;
	
	
	private NavbarHardLinks(String viewName) {
		this.viewName = viewName;
	}
	
	/**
	 * @return the view name
	 */
	public String getViewName() {
		return this.viewName;
	}
}
