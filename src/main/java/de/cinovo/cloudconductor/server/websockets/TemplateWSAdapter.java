package de.cinovo.cloudconductor.server.websockets;

import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.model.Template;
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
@WebSocket(pathSpec = "/template")
public class TemplateWSAdapter extends ASimpleWSAdapter<WSHeartbeat, Template> {
	
	@Autowired
	protected ASimpleWSHandler<TemplateWSAdapter, Template> websocketHandler;
	
	
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
