package de.cinovo.cloudconductor.server.dao.hibernate;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.model.IVersionized;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 * @param <E> the entity
 */
public abstract class AVersionedEntityHib<E extends IVersionized<Long>> extends AAuditedEntityHib<E, Long> {
	
	@Override
	@Transactional
	public E save(final E element, String auditMessage) {
		if ((element.getId() == null) || (element.getId() < 0)) {
			return this.saveNewElement(element);
		}
		if (auditMessage == null) {
			return super.save(this.createNewRevision(element), this.getChangeEntry(element));
		}
		return super.save(this.createNewRevision(element), auditMessage);
	}
	
	@Override
	@Transactional
	public E save(E element) {
		return this.save(element, null);
	}
	
	@Transactional
	private E createNewRevision(E element) {
		E r = (E) element.cloneNew();
		if ((r.getVersion() == null) || (r.getVersion() < 0)) {
			r.setVersion(0L);
		} else {
			r.setVersion(element.getVersion() + 1);
		}
		r.setDeleted(false);
		r.setOrigId(element.getOrigId());
		return r;
	}
	
	@Transactional
	private E saveNewElement(E element) {
		element.setVersion(0L);
		element.setDeleted(false);
		E ele = super.save(element);
		ele.setOrigId(ele.getId());
		return this.entityManager.merge(ele);
	}
	
	@Override
	@Transactional
	public void delete(final E element, String auditMessage) {
		element.setDeleted(true);
		super.save(element, auditMessage);
	}
	
	@Override
	@Transactional
	public void delete(final E element) {
		element.setDeleted(true);
		super.save(element);
	}
	
	@Override
	@Transactional
	public void deleteById(final Long id) {
		final E element = this.findById(id);
		if (element == null) {
			throw new EntityNotFoundException();
		}
		this.delete(element);
	}
	
	@Transactional
	protected List<E> findVersionedList() {
		final TypedQuery<E> query = this.entityManager.createQuery(this.getFindAllListQuery(), this.getEntityClass());
		return query.getResultList();
	}
	
	@Transactional
	protected final E findVersionedByQuery(final String query, String as, final Object... params) {
		return super.findByQuery(this.getVersionizedQuerry(query, as), params);
	}
	
	@Transactional
	protected final List<E> findVersionedListByQuery(final String query, String as, final Object... params) {
		return super.findListByQuery(this.getVersionizedQuerry(query, as), params);
	}
	
	@Transactional
	protected String getVersionizedQuerry(String query, String as) {
		String maxVersion = "SELECT MAX(second.version) FROM  " + this.getEntityClass().getSimpleName() + " as second WHERE second.origId = " + as + ".origId";
		StringBuilder newQuery = new StringBuilder();
		newQuery.append(query);
		if (query.contains("WHERE")) {
			newQuery.append(" AND ");
		} else {
			newQuery.append(" as " + as + " WHERE ");
		}
		newQuery.append(as);
		newQuery.append(".deleted=false AND ");
		newQuery.append(as);
		newQuery.append(".version=(");
		newQuery.append(maxVersion);
		newQuery.append(")");
		return newQuery.toString();
	}
	
	@Override
	@Transactional
	protected String getFindListQuery() {
		return this.getVersionizedQuerry("FROM " + this.getEntityClass().getSimpleName(), "first");
	}
	
	@Transactional
	protected String getFindAllListQuery() {
		return "FROM " + this.getEntityClass().getSimpleName();
	}
}
