package de.cinovo.cloudconductor.server.ws.host;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.websockets.model.WSHeartbeat;
import de.cinovo.cloudconductor.server.ws.AParamWSAdapter;
import de.taimos.dvalin.jaxrs.websocket.WebSocket;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@WebSocket(pathSpec = "/host")
public class HostDetailWSAdapter extends AParamWSAdapter<Host> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HostDetailWSAdapter.class);
	
	
	@Override
	protected void onWebSocketObject(WSHeartbeat message) {
		// do nothing
	}
	
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		HostDetailWSAdapter.LOGGER.debug("Host Detail WS connected!");
	}
	
	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		super.onWebSocketClose(statuscode, reason);
		HostDetailWSAdapter.LOGGER.debug("Host Detail WS closed: {} Reason {}", statuscode, reason);
	}
	
}
