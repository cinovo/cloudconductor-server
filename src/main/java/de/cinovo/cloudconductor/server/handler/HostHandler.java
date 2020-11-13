package de.cinovo.cloudconductor.server.handler;

import com.google.common.base.Strings;
import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
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
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	
	@Autowired
	private HostsWSHandler hostWSHandler;
	@Autowired
	private HostDetailWSHandler hostDetailWSHandler;
	
	@Autowired
	private IAgentDAO agentDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	
	/**
	 * @param template the template which hosts should be updated
	 */
	public void updateHostDetails(ETemplate template) {
		for (EHost host : this.hostDAO.findHostsForTemplate(template.getId())) {
			this.hostDetailWSHandler.broadcastChange(host, ChangeType.UPDATED);
		}
	}
	
	/**
	 * @param host        the host to change the state of the service in
	 * @param serviceName the service to change
	 * @param state       the desired state
	 * @return the modified host
	 */
	public EHost changeServiceState(EHost host, String serviceName, ServiceState state) {
		if (host == null) {
			return null;
		}
		if (Strings.isNullOrEmpty(serviceName) || (state == null)) {
			return host;
		}
		EService service = this.serviceDAO.findByName(serviceName);
		if (service != null) {
			EServiceState currentServiceState = this.serviceStateDAO.findByServiceAndHost(service.getId(), host.getId());
			if (currentServiceState.getState().isStateChangePossible(state)) {
				currentServiceState.setState(state);
			} else {
				this.logger.warn(String.format("Desired target state of service '%s' in host '%s' not reachable.", serviceName, host.getName()));
			}
		}
		return host;
	}
	
	/**
	 * Creates and persists a new host with given name and template.
	 *
	 * @param hostName the name for the new host
	 * @param template the template to be used by the new host
	 * @param agent
	 * @return the new host
	 */
	public EHost createNewHost(String hostName, ETemplate template, EAgent agent) {
		EHost newHost = new EHost();
		newHost.setName(hostName);
		newHost.setTemplateId(template.getId());
		newHost.setLastSeen((new DateTime()).getMillis());
		newHost.setUuid(UUID.randomUUID().toString());
		if (agent != null) {
			newHost.setAgentId(agent.getId());
		}
		newHost = this.hostDAO.save(newHost);
		this.hostDetailWSHandler.broadcastChange(newHost, ChangeType.UPDATED);
		
		this.hostWSHandler.broadcastEvent(newHost, ChangeType.ADDED);
		return newHost;
	}
	
	/**
	 * @param eHost       the host to modify
	 * @param newTemplate the new template to move to
	 * @return the modified host
	 */
	public EHost moveHostToNewTemplate(EHost eHost, String newTemplate) {
		if (eHost == null) {
			return null;
		}
		ETemplate newTemp = this.templateDAO.findByName(newTemplate);
		if (newTemp == null) {
			return eHost;
		}
		if (eHost.getTemplateId().equals(newTemp.getId())) {
			return eHost;
		}
		eHost.setTemplateId(newTemp.getId());
		return this.hostDAO.save(eHost);
	}
}
