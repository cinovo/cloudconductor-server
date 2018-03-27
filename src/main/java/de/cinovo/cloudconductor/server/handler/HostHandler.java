package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.host.HostsWSHandler;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class HostHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private ITemplateDAO templateDAO;

	@Autowired
	private HostsWSHandler hostWSHandler;

	@Autowired
	private HostDetailWSHandler hostDetailWSHandler;


	/**
	 * @param template the template which hosts should be updated
	 */
	public void updateHostDetails(ETemplate template) {
		for(EHost host : template.getHosts()) {
			this.hostDetailWSHandler.broadcastChange(host.getName(), new WSChangeEvent<Host>(ChangeType.UPDATED, host.toApi()));
		}
	}

	/**
	 * @param host    the host to change the state of the service in
	 * @param service the service to change
	 * @param state   the desired state
	 * @return the modified host
	 */
	public EHost changeServiceState(EHost host, String service, ServiceState state) {
		if(host == null) {
			return null;
		}
		if((service == null) || service.isEmpty() || (state == null)) {
			return host;
		}

		for(EServiceState currentServiceState : host.getServices()) {
			if(currentServiceState.getService().getName().equals(service)) {
				if(currentServiceState.getState().isStateChangePossible(state)) {
					currentServiceState.setState(state);
				} else {
					this.logger.warn("Desired target state of service " + service + " in host " + host.getName() + " not reachable.");
				}
				break;
			}
		}
		return host;
	}

	/**
	 * Creates and persists a new host with given name and template.
	 *
	 * @param hostName the name for the new host
	 * @param template the template to be used by the new host
	 * @return the new host
	 */
	public EHost createNewHost(String hostName, ETemplate template) {
		EHost newHost = new EHost();
		newHost.setName(hostName);
		newHost.setTemplate(template);
		newHost.setLastSeen((new DateTime()).getMillis());
		newHost.setUuid(UUID.randomUUID().toString());
		newHost = this.hostDAO.save(newHost);
		this.hostDetailWSHandler.broadcastChange(hostName, new WSChangeEvent<>(ChangeType.UPDATED, newHost.toApi()));
		SimpleHost simpleHost = this.hostDAO.findSimpleHost(newHost.getId());
		if(simpleHost != null) {
			this.hostWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.ADDED, simpleHost));
		}
		return newHost;
	}


	/**
	 * @param eHost       the host to modify
	 * @param newTemplate the new template to move to
	 * @return the modified host
	 */
	public EHost moveHostToNewTemplate(EHost eHost, String newTemplate) {
		if(eHost == null) {
			return null;
		}
		if(eHost.getTemplate().getName().equalsIgnoreCase(newTemplate)) {
			return eHost;
		}

		ETemplate newTemp = this.templateDAO.findByName(newTemplate);
		if(newTemp != null) {
			eHost.setTemplate(newTemp);
			return this.hostDAO.save(eHost);
		}
		return eHost;
	}
}
