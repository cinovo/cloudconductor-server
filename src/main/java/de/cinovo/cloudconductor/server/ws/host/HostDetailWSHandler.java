package de.cinovo.cloudconductor.server.ws.host;

import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.AParamWSAdapter;
import de.cinovo.cloudconductor.server.ws.AParamWSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class HostDetailWSHandler extends AParamWSHandler<AParamWSAdapter<Host>, Host> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HostDetailWSHandler.class);
	
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IAgentDAO agentDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	
	@Override
	public void addSocketAdapter(String hostName, AParamWSAdapter<Host> a) {
		super.addSocketAdapter(hostName, a);
		HostDetailWSHandler.LOGGER.debug("Added WS for details of host '{}'", hostName);
	}
	
	@Override
	public boolean removeSocketAdapter(String hostName, AParamWSAdapter<Host> a) {
		boolean success = super.removeSocketAdapter(hostName, a);
		HostDetailWSHandler.LOGGER.debug("Removed WS for details of host '{}'", hostName);
		return success;
	}
	
	/**
	 * @param host the host
	 * @param type the type
	 */
	public void broadcastChange(EHost host, ChangeType type) {
		WSChangeEvent<Host> twsChangeEvent = new WSChangeEvent<>(type, host.toApi(this.serviceStateDAO, this.agentDAO, this.packageStateDAO, this.templateDAO));
		super.broadcastChange(host.getUuid(), twsChangeEvent);
		HostDetailWSHandler.LOGGER.debug("Broadcasted change for host '{}'", host.getUuid());
	}
	
}
