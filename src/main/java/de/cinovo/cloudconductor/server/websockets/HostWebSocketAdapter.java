package de.cinovo.cloudconductor.server.websockets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSHeartbeat;
import de.taimos.dvalin.jaxrs.websocket.ServerJSONWebSocketAdapter;
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
public class HostWebSocketAdapter extends ServerJSONWebSocketAdapter<WSHeartbeat> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HostWebSocketAdapter.class);
	
	@Autowired
	private HostWebSocketHandler wsHandler;
	
	@Value("${ws.timeout:60000}")
	private long websocketTimeout;
	
	private List<String> hostNames = new ArrayList<>();
	
	
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
				this.hostNames.add(name);
				this.wsHandler.addSocket(name, this);
				HostWebSocketAdapter.LOGGER.info("WebSocket connected for name {}", name);
			}
		} else {
			HostWebSocketAdapter.LOGGER.error("Error on WS connect: Missing parameters!");
		}
	}
	
	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		super.onWebSocketClose(0, reason);
		
		for (String hostName : this.hostNames) {
			this.wsHandler.removeSocket(hostName, this);
		}
		
		HostWebSocketAdapter.LOGGER.info("WebSocket disconnected: Status {} Reason {}", statuscode, reason);
	}
	
	/**
	 * @param event the event to be sent via WS
	 */
	public void sendChangeEvent(WSChangeEvent<Host> event) {
		this.sendObjectToSocket(event);
		HostWebSocketAdapter.LOGGER.info("Send change event {}", event);
	}
}
