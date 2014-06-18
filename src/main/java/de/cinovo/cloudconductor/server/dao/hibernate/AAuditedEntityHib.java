package de.cinovo.cloudconductor.server.dao.hibernate;

import java.security.Principal;

import javax.persistence.EntityNotFoundException;

import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.security.SecurityContext;
import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.INamed;
import de.cinovo.cloudconductor.server.dao.IAuditedEntity;
import de.cinovo.cloudconductor.server.model.EAuditLog;
import de.cinovo.cloudconductor.server.model.IVersionized;
import de.cinovo.cloudconductor.server.model.enums.AuditCategory;
import de.cinovo.cloudconductor.server.model.enums.AuditType;
import de.taimos.dao.IEntity;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 * @param <E> the entity type
 * @param <I> the id type
 */
public abstract class AAuditedEntityHib<E extends IEntity<I>, I> extends EntityDAOHibernate<E, I> implements IAuditedEntity<E, I> {
	
	/**
	 * @return the audit category of the element
	 */
	protected abstract AuditCategory getAuditCategory();
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public E save(final E element, String auditMessage) {
		E result = super.save(element);
		AuditType type = this.getAuditType(element);
		if ((element instanceof IVersionized) && (((IVersionized<?>) element).getVersion() instanceof Number)) {
			this.generateAuditLog(type, auditMessage, element.getId(), ((IVersionized<Number>) element).getVersion(), ((IVersionized<Number>) result).getVersion());
		} else {
			this.generateAuditLog(type, auditMessage, element.getId());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	private AuditType getAuditType(E element) {
		if (element instanceof IVersionized) {
			IVersionized<Long> versioned = (IVersionized<Long>) element;
			if (versioned.isDeleted()) {
				return AuditType.DELETE;
			}
			if ((versioned.getId() == null) && (versioned.getVersion() < 1)) {
				return AuditType.NEW;
			}
			return AuditType.CHANGE;
		}
		if ((element.getId() == null)) {
			return AuditType.NEW;
		}
		return AuditType.CHANGE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public E save(final E element) {
		E result = super.save(element);
		AuditType type = this.getAuditType(element);
		String auditMessage;
		switch (type) {
		case CHANGE:
			auditMessage = this.getChangeEntry(element);
			break;
		case DELETE:
			auditMessage = this.getDeleteEntry(element);
			break;
		case NEW:
			auditMessage = this.getNewEntry(element);
			break;
		default:
			auditMessage = "";
			break;
		}
		if ((element instanceof IVersionized) && (((IVersionized<?>) element).getVersion() instanceof Number)) {
			this.generateAuditLog(type, auditMessage, element.getId(), ((IVersionized<Number>) element).getVersion(), ((IVersionized<Number>) result).getVersion());
		} else {
			this.generateAuditLog(type, auditMessage, element.getId());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void delete(final E element, String auditMessage) {
		super.delete(element);
		if ((element instanceof IVersionized) && (((IVersionized<?>) element).getVersion() instanceof Number)) {
			this.generateAuditLog(AuditType.DELETE, auditMessage, element.getId(), ((IVersionized<Number>) element).getVersion(), null);
		} else {
			this.generateAuditLog(AuditType.DELETE, auditMessage, element.getId(), null, null);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void delete(final E element) {
		super.delete(element);
		if ((element instanceof IVersionized) && (((IVersionized<?>) element).getVersion() instanceof Number)) {
			this.generateAuditLog(AuditType.DELETE, this.getDeleteEntry(element), element.getId(), ((IVersionized<Number>) element).getVersion(), null);
		} else {
			this.generateAuditLog(AuditType.DELETE, this.getDeleteEntry(element), element.getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void deleteById(final I id) {
		final E element = this.findById(id);
		if (element == null) {
			throw new EntityNotFoundException();
		}
		this.entityManager.remove(element);
		if ((element instanceof IVersionized) && (((IVersionized<?>) element).getVersion() instanceof Number)) {
			this.generateAuditLog(AuditType.DELETE, this.getDeleteEntry(element), element.getId(), ((IVersionized<Number>) element).getVersion(), null);
		} else {
			this.generateAuditLog(AuditType.DELETE, this.getDeleteEntry(element), element.getId());
		}
	}
	
	private String getUser() {
		SecurityContext sc = PhaseInterceptorChain.getCurrentMessage().get(SecurityContext.class);
		Principal p = sc.getUserPrincipal();
		if (p == null) {
			return "REST-CALL";
		}
		return p.getName();
	}
	
	private void generateAuditLog(AuditType type, String message, I elementId) {
		this.generateAuditLog(type, message, elementId, null, null);
	}
	
	private void generateAuditLog(AuditType type, String message, I elementId, Number origRev, Number newRev) {
		EAuditLog log = new EAuditLog();
		log.setAuditType(type);
		log.setCategory(this.getAuditCategory());
		log.setTimestamp(DateTime.now().getMillis());
		log.setUsername(this.getUser());
		log.setEntry(message);
		if ((elementId != null) && (elementId instanceof Number)) {
			log.setElementId(((Number) elementId).longValue());
		}
		if (origRev != null) {
			log.setOrigRev(origRev.longValue());
		}
		if (newRev != null) {
			log.setNewRev(newRev.longValue());
		}
		this.entityManager.merge(log);
	}
	
	protected String getDeleteEntry(E element) {
		if (element instanceof INamed) {
			return "Deleted element " + ((INamed) element).getName();
		}
		return "Deleted element";
	}
	
	protected String getChangeEntry(E element) {
		if (element instanceof INamed) {
			return "Changed element " + ((INamed) element).getName();
		}
		return "Changed element";
	}
	
	protected String getNewEntry(E element) {
		if (element instanceof INamed) {
			return "New element " + ((INamed) element).getName() + " created";
		}
		return "New element created";
	}
}
