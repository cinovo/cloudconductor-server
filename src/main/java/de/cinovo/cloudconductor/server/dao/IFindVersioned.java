package de.cinovo.cloudconductor.server.dao;

import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 * @param <T> the type
 * @param <I> the primary key type
 */
public interface IFindVersioned<T extends IEntity<I>, I> {
	
	/**
	 * @param id the primary key
	 * @param revision the revision
	 * @return the element
	 */
	public T findVersion(I id, long revision);
	
	/**
	 * @param id the primary key
	 * @return the latest element
	 */
	public T findLatest(I id);
}
