package de.cinovo.cloudconductor.server.web2.helper;

public class ViewFilter implements Comparable<ViewFilter> {
	
	private String id;
	private String name;
	private boolean isDefault;
	
	
	/**
	 * @param id
	 * @param name
	 * @param isDefault
	 */
	public ViewFilter(String id, String name, boolean isDefault) {
		this.id = id;
		this.name = name;
		this.isDefault = isDefault;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return this.isDefault;
	}
	
	@Override
	public int compareTo(ViewFilter o) {
		return this.name.compareTo(o.getName());
	}
	
}
