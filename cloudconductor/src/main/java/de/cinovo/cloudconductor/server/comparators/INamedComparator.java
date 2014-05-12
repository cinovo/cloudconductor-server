package de.cinovo.cloudconductor.server.comparators;

import java.util.Comparator;

import de.cinovo.cloudconductor.api.model.INamed;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class INamedComparator implements Comparator<INamed> {
	
	@Override
	public int compare(INamed arg0, INamed arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}
	
}
