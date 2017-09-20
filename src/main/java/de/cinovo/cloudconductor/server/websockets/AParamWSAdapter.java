package de.cinovo.cloudconductor.server.websockets;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSHeartbeat;
import de.taimos.dvalin.jaxrs.websocket.ServerJSONWebSocketAdapter;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 * @param <T> the type of object to be sent via WS
 *
 */
public class AParamWSAdapter<T> extends ServerJSONWebSocketAdapter<WSHeartbeat> {
	
	@Value("${ws.timeout:60000}")
	protected long websocketTimeout;
	
	protected List<String> names = new ArrayList<>();
	
	
	@Override
	protected Class<WSHeartbeat> getObjectType() {
		return WSHeartbeat.class;
	}
	
	@Override
	protected void onWebSocketObject(WSHeartbeat message) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param event the event to be sent via WS
	 */
	public void sendChangeEvent(WSChangeEvent<T> event) {
		this.sendObjectToSocket(event);
	}
	
}
