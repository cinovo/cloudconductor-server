package de.cinovo.cloudconductor.server.rest.agent;

import de.cinovo.cloudconductor.server.handler.AgentHandler;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IAgent;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class AgentImpl implements IAgent {
	
	@Autowired
	private AgentHandler agentHandler;
	
	
	@Override
	public PackageStateChanges notifyPackageState(String template, String host, PackageState rpmState, String uuid) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(host);
		RESTAssert.assertNotEmpty(uuid);
		RESTAssert.assertNotNull(rpmState);
		
		PackageStateChanges changes = this.agentHandler.handlePackageState(host, template, rpmState);
		
		return changes;
	}
	
	@Override
	public ServiceStatesChanges notifyServiceState(String template, String host, ServiceStates serviceState, String uuid) {
		RESTAssert.assertNotEmpty(host);
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotNull(serviceState);
		RESTAssert.assertNotEmpty(uuid);
		
		ServiceStatesChanges changes = this.agentHandler.handleServiceState(host, template, serviceState, uuid);
		return changes;
	}
	
	@Override
	public AgentOption heartBeat(String template, String host, String agent, String uuid) {
		RESTAssert.assertNotEmpty(host);
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(agent);
		RESTAssert.assertNotEmpty(uuid);
		
		AgentOption option = this.agentHandler.handleHeartBeat(template, host, agent, uuid);
		
		RESTAssert.assertNotNull(option.getUuid());
		return option;
	}
	
}
