package de.cinovo.cloudconductor.server.websockets;

import java.util.Set;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class TemplateWebSocketHandler {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Set<TemplateWebSocketAdapter> connectedSockets = new ConcurrentHashSet<>();
	
	
	/**
	 * 
	 * @param newWSAdapter the WebSocketAdapter to add
	 */
	public void addSocketAdapter(TemplateWebSocketAdapter newWSAdapter) {
		this.connectedSockets.add(newWSAdapter);
	}
	
	/**
	 * 
	 * @param wsAdapter the WebSocketAdapter to remove
	 */
	public void removeSocketAdapter(TemplateWebSocketAdapter wsAdapter) {
		this.connectedSockets.remove(wsAdapter);
	}
	
	/**
	 * @param event the event to be sent
	 */
	public void broadcast(WSChangeEvent<Template> event) {
		this.logger.info("Broadcast message {}", event);
		for (TemplateWebSocketAdapter adapter : this.connectedSockets) {
			adapter.sendChangeEvent(event);
		}
	}
	
}
