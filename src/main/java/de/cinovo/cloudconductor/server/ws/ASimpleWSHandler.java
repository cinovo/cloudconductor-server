package de.cinovo.cloudconductor.server.ws;

import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.Set;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 * @param <A> the type of WS adapter
 * @param <T> the type of the objects to transfer
 */
public abstract class ASimpleWSHandler<A extends ASimpleWSAdapter<?, T>, T> {
	
	private Set<A> connectedWS = new ConcurrentHashSet<>();
	
	
	/**
	 * @param wsAdapter the WS adapter to add
	 */
	public void addSocketAdapter(A wsAdapter) {
		this.connectedWS.add(wsAdapter);
	}
	
	/**
	 * @param wsAdapter the WS adapter to remove
	 */
	public void removeSocketAdapter(A wsAdapter) {
		this.connectedWS.remove(wsAdapter);
	}
	
	/**
	 * @param changeEvent the event to be sent via all WS connections
	 */
	public void broadcastEvent(WSChangeEvent<T> changeEvent) {
		for (A adapter : this.connectedWS) {
			adapter.sendChangeEvent(changeEvent);
		}
	}
	
	
	
}
