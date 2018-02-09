package de.cinovo.cloudconductor.server.ws.host;

import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.websockets.model.WSHeartbeat;
import de.cinovo.cloudconductor.server.ws.ASimpleWSAdapter;
import de.cinovo.cloudconductor.server.ws.ASimpleWSHandler;
import de.taimos.dvalin.jaxrs.websocket.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@WebSocket(pathSpec = "/hosts")
public class HostsWSAdapter extends ASimpleWSAdapter<WSHeartbeat, SimpleHost> {
	
	@Autowired
	protected ASimpleWSHandler<HostsWSAdapter, SimpleHost> websocketHandler;
	
	
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		sess.setIdleTimeout(this.websocketTimeout);
		this.websocketHandler.addSocketAdapter(this);
	}
	
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		this.websocketHandler.removeSocketAdapter(this);
	}
	
	@Override
	protected Class<WSHeartbeat> getObjectType() {
		return WSHeartbeat.class;
	}
	
}
