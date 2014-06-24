package de.cinovo.cloudconductor.server.model.enums;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public enum AuditType {
	/** backwards compatibility */
	UNKNOWN,
	/** generated a new entry */
	NEW,
	/** generated a change */
	CHANGE,
	/** deleted something */
	DELETE
}
