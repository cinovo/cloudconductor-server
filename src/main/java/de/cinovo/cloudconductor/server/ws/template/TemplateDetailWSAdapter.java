package de.cinovo.cloudconductor.server.ws.template;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cinovo.cloudconductor.api.model.Template;
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
@WebSocket(pathSpec = "/template")
public class TemplateDetailWSAdapter extends AParamWSAdapter<Template> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDetailWSAdapter.class);
	
	
	@Override
	protected void onWebSocketObject(WSHeartbeat message) {
		// do nothing
	}
	
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		TemplateDetailWSAdapter.LOGGER.info("Template Detail WS connected!");
	}
	
	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		super.onWebSocketClose(statuscode, reason);
		TemplateDetailWSAdapter.LOGGER.info("Template Detail WS closed: {} Reason", statuscode, reason);
	}
	
}
