package de.cinovo.cloudconductor.server.ws;

import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.taimos.dvalin.jaxrs.websocket.ServerJSONWebSocketAdapter;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 * @param <R> the type of objects to receive
 * @param <S> the type of objects to send
 */
public abstract class ASimpleWSAdapter<R, S> extends ServerJSONWebSocketAdapter<R> {
	
	@Value("${ws.timeout:60000}")
	protected long websocketTimeout;
	
	
	@Override
	protected void onWebSocketObject(R message) {
		//
	}
	
	/**
	 * @param changeEvent the event to be sent
	 */
	public void sendChangeEvent(WSChangeEvent<S> changeEvent) {
		this.sendObjectToSocket(changeEvent);
	}
	
}
