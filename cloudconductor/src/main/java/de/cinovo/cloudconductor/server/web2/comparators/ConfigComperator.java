package de.cinovo.cloudconductor.server.web2.comparators;

import java.util.Comparator;

import de.cinovo.cloudconductor.server.model.config.EConfigValue;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigComperator implements Comparator<EConfigValue> {
	
	@Override
	public int compare(EConfigValue arg0, EConfigValue arg1) {
		if (!arg0.getTemplate().equals(arg1.getTemplate())) {
			return arg0.getTemplate().compareTo(arg1.getTemplate());
		}
		String a0s = (arg0.getService() == null) || arg0.getService().isEmpty() ? null : arg0.getService();
		String a1s = (arg1.getService() == null) || arg1.getService().isEmpty() ? null : arg1.getService();
		
		if ((a0s == null) && (a1s == null)) {
			return arg0.getConfigkey().compareTo(arg1.getConfigkey());
		}
		
		if ((a0s == null) && (a1s != null)) {
			return 1;
		}
		if ((a0s != null) && (a1s == null)) {
			return -1;
		}
		
		if (a0s.equals(a1s)) {
			return arg0.getConfigkey().compareTo(arg1.getConfigkey());
		}
		
		return a0s.compareTo(a1s);
	}
}
