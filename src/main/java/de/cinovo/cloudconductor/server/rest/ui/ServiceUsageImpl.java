/**
 * 
 */
package de.cinovo.cloudconductor.server.rest.ui;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IServiceUsage;
import de.cinovo.cloudconductor.server.handler.ServiceHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class ServiceUsageImpl implements IServiceUsage {
	
	@Autowired
	private ServiceHandler serviceHandler;
	
	
	@Override
	public Map<String, Map<String, String>> getServiceUsages() {
		return this.serviceHandler.getServiceUsage();
	}
	
}
