package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class HostHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param host the host to change the state of the service in
	 * @param service the service to change
	 * @param state the desired state
	 * @return the modified host
	 */
	public EHost changeServiceState(EHost host, String service, ServiceState state) {
		if(host == null) {
			return null;
		}
		if(service == null || service.isEmpty() || state == null) {
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
}
