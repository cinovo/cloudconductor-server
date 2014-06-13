package de.cinovo.cloudconductor.server.comparators;

import java.util.Comparator;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.model.EServiceState;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class StateComparator implements Comparator<EServiceState> {
	
	@Override
	@Transactional
	public int compare(EServiceState arg0, EServiceState arg1) {
		return arg0.getService().getName().toLowerCase().compareTo(arg1.getService().getName().toLowerCase());
	}
	
}
