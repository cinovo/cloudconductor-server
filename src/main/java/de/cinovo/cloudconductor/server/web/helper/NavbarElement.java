package de.cinovo.cloudconductor.server.web.helper;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class NavbarElement implements Comparable<NavbarElement> {
	
	private String identifier;
	private String path;
	private int position = -1;
	
	
	/**
	 * @param identifier the identifier
	 * @param path the path (url)
	 */
	public NavbarElement(String identifier, String path) {
		this.identifier = identifier;
		this.path = path;
	}
	
	/**
	 * @param identifier the identifier
	 * @param path the path (url)
	 * @param orderNo the order number
	 */
	public NavbarElement(String identifier, String path, int orderNo) {
		this.identifier = identifier;
		this.path = path;
		this.position = orderNo;
	}
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * @return the path (url)
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * @return the ordernumber/position fo the item
	 */
	public int getPosition() {
		return this.position;
	}
	
	@Override
	public int compareTo(NavbarElement other) {
		int result = 0;
		if (this.position > 0) {
			result = Integer.compare(this.position, other.getPosition());
		}
		if (result == 0) {
			if (this.identifier == null) {
				return -1;
			}
			if (other.identifier == null) {
				return 1;
			}
			return this.identifier.compareTo(other.identifier);
		}
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NavbarElement)) {
			return false;
		}
		if (!this.identifier.equals(((NavbarElement) obj).identifier)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.identifier.hashCode();
	}
}