package de.cinovo.cloudconductor.server.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class AParamWSAdapter<T> extends ServerJSONWebSocketAdapter<WSHeartbeat> {
	
	@Value("${ws.timeout:60000}")
	protected long websocketTimeout;
	
	protected List<String> names = new ArrayList<>();
	
	@Autowired
	protected AParamWSHandler<AParamWSAdapter<T>, T> wsHandler;
	
	
	@Override
	protected Class<WSHeartbeat> getObjectType() {
		return WSHeartbeat.class;
	}
	
	@Override
	public void onWebSocketConnect(Session session) {
		super.onWebSocketConnect(session);
		session.setIdleTimeout(this.websocketTimeout);
		
		Map<String, List<String>> parameters = session.getUpgradeRequest().getParameterMap();
		if ((parameters != null) && (parameters.get("name") != null)) {
			
			for (String name : parameters.get("name")) {
				this.names.add(name);
				this.wsHandler.addSocketAdapter(name, this);
			}
		}
	}
	
	@Override
	public void onWebSocketClose(int statuscode, String reason) {
		super.onWebSocketClose(statuscode, reason);
		
		for (String hostName : this.names) {
			this.wsHandler.removeSocketAdapter(hostName, this);
		}
	}
	
	/**
	 * @param event the event to be sent via WS
	 */
	public void sendChangeEvent(WSChangeEvent<T> event) {
		this.sendObjectToSocket(event);
	}
	
}
