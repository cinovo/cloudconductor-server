package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IServiceUsage;
import de.cinovo.cloudconductor.server.handler.ServiceHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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
