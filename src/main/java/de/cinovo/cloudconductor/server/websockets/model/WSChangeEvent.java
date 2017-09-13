package de.cinovo.cloudconductor.server.websockets.model;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 * @param <T> the type of objects to transfer
 */
public class WSChangeEvent<T> {
	
	/**
	 *
	 */
	public enum ChangeType {
		/**
		 * Indicates element was added
		 */
		ADDED,
		/**
		 * Indicates element was changed
		 */
		UPDATED,
		/**
		 * Indicates element was deleted
		 */
		DELETED,
	}
	
	
	private ChangeType type;
	private T content;
	
	
	/**
	 * Create an empty change event
	 */
	public WSChangeEvent() {
		
	}
	
	/**
	 * 
	 * @param type the type of the change event to set
	 * @param content the content of the change event to set
	 */
	public WSChangeEvent(ChangeType type, T content) {
		this.type = type;
		this.content = content;
	}
	
	/**
	 * @return the type of change event
	 */
	public ChangeType getType() {
		return this.type;
	}
	
	/**
	 * @param type the type of change event to set
	 */
	public void setType(ChangeType type) {
		this.type = type;
	}
	
	/**
	 * @return the content of the event
	 */
	public T getContent() {
		return this.content;
	}
	
	/**
	 * @param content the content of the event to set
	 */
	public void setContent(T content) {
		this.content = content;
	}
	
}
