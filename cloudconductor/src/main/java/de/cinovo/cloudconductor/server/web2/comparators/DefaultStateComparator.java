package de.cinovo.cloudconductor.server.web2.comparators;

import java.util.Comparator;

import de.cinovo.cloudconductor.server.model.EServiceDefaultState;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class DefaultStateComparator implements Comparator<EServiceDefaultState> {
	
	@Override
	public int compare(EServiceDefaultState arg0, EServiceDefaultState arg1) {
		return arg0.getService().getName().toLowerCase().compareTo(arg1.getService().getName().toLowerCase());
	}
	
}
