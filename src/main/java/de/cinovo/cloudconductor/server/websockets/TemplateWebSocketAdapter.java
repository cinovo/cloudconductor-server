package de.cinovo.cloudconductor.server.websockets;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.api.model.Template;
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
@WebSocket(pathSpec = "/template")
public class TemplateWebSocketAdapter extends ServerJSONWebSocketAdapter<WSHeartbeat> {
	
	@Autowired
	private TemplateWebSocketHandler websocketHandler;
	
	@Value("${ws.timeout:60000}")
	private long websocketTimeout;
	
	private Logger logger = LoggerFactory.getLogger(TemplateWebSocketAdapter.class);
	
	
	@Override
	protected Class<WSHeartbeat> getObjectType() {
		return WSHeartbeat.class;
	}
	
	@Override
	protected void onWebSocketObject(WSHeartbeat h) {
		//
	}
	
	/**
	 * @param event to send via WebSocket
	 */
	public void sendChangeEvent(WSChangeEvent<Template> event) {
		this.sendObjectToSocket(event);
	}
	
	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		sess.setIdleTimeout(this.websocketTimeout);
		this.websocketHandler.addSocketAdapter(this);
		this.logger.info("WebSocket connected!");
	}
	
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		this.websocketHandler.removeSocketAdapter(this);
		this.logger.info("WebSocket disconnected!");
	}
	
}
