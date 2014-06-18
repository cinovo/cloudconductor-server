package de.cinovo.cloudconductor.server.dao;

import de.taimos.dao.IEntity;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 * @param <E> the entity type
 * @param <I> the id type
 */
public interface IAuditedEntity<E extends IEntity<I>, I> extends IEntityDAO<E, I> {
	
	/**
	 * @param element the element
	 * @param auditMessage the customized audit message
	 * @return the saved element
	 */
	public E save(final E element, String auditMessage);
	
	/**
	 * @param element the element
	 * @param auditMessage the customized audit message
	 */
	public void delete(final E element, String auditMessage);
}
