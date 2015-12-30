package de.cinovo.cloudconductor.server.model;

import java.io.Serializable;

import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * @param <I> version class
 * 
 */
public interface IVersionized<I> extends IEntity<I>, Serializable {
	
	/**
	 * @return the version of the element
	 */
	I getVersion();
	
	/**
	 * @param version the version
	 */
	void setVersion(I version);
	
	/**
	 * @return whether the version has been deleted or not
	 */
	boolean isDeleted();
	
	/**
	 * @param deleted state
	 */
	void setDeleted(boolean deleted);
	
	/**
	 * @param id the id
	 */
	void setId(I id);
	
	/**
	 * @param id the original id
	 */
	void setOrigId(I id);
	
	/**
	 * @return the original id
	 */
	I getOrigId();
	
	/**
	 * @return the cloned object
	 */
	IEntity<I> cloneNew();
}
