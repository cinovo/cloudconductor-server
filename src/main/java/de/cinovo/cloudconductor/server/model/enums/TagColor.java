package de.cinovo.cloudconductor.server.model.enums;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public enum TagColor {
	/***/
	GREEN("success"),
	/***/
	BLUE("info"),
	/***/
	ORANGE("warning"),
	/***/
	RED("danger");
	
	private String css;
	
	
	private TagColor(String css) {
		this.css = css;
	}
	
	/**
	 * @return css category
	 */
	public String getCSS() {
		return this.css;
	}
}
