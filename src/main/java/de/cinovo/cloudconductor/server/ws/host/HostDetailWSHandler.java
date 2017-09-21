package de.cinovo.cloudconductor.server.ws.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.ws.AParamWSAdapter;
import de.cinovo.cloudconductor.server.ws.AParamWSHandler;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class HostDetailWSHandler extends AParamWSHandler<AParamWSAdapter<Host>, Host> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HostDetailWSHandler.class);
	
	
	@Override
	public void addSocketAdapter(String hostName, AParamWSAdapter<Host> a) {
		super.addSocketAdapter(hostName, a);
		HostDetailWSHandler.LOGGER.info("Added WS for details of host {}", hostName);
	}
	
	@Override
	public boolean removeSocketAdapter(String hostName, AParamWSAdapter<Host> a) {
		boolean success = super.removeSocketAdapter(hostName, a);
		HostDetailWSHandler.LOGGER.info("Removed WS for details of host {}", hostName);
		return success;
	}
	
	@Override
	public void broadcastChange(String hostName, WSChangeEvent<Host> event) {
		super.broadcastChange(hostName, event);
		HostDetailWSHandler.LOGGER.info("Broadcasted change for host {}", hostName);
	}
	
}
