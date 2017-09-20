package de.cinovo.cloudconductor.server.websockets;

import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.websockets.model.WSHeartbeat;
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
	
	@Autowired
	private HostDetailWSHandler wsHandler;
	
	
	@Override
	protected Class<WSHeartbeat> getObjectType() {
		return WSHeartbeat.class;
	}
	
	@Override
	protected void onWebSocketObject(WSHeartbeat message) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		sess.setIdleTimeout(this.websocketTimeout);
		
		Map<String, List<String>> parameters = sess.getUpgradeRequest().getParameterMap();
		if ((parameters != null) && (parameters.get("name") != null)) {
			
			for (String name : parameters.get("name")) {
				this.names.add(name);
				this.wsHandler.addSocket(name, this);
			}
		}
	}
	
	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		super.onWebSocketClose(0, reason);
		
		for (String hostName : this.names) {
			this.wsHandler.removeSocket(hostName, this);
		}
		
		HostDetailWSAdapter.LOGGER.info("WebSocket disconnected: Status {} Reason {}", statuscode, reason);
	}
	
}
