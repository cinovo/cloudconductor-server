package de.cinovo.cloudconductor.server.web2.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public enum NavbarHardLinks {
	
	config("Configuration"),
	
	options("Options"),
	
	links("Links");
	
	private String viewName;
	
	
	private NavbarHardLinks(String viewName) {
		this.viewName = viewName;
	}
	
	public String getViewName() {
		return this.viewName;
	}
}
