package de.cinovo.cloudconductor.server.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 * @param <A> the type of WS adapter to be handled
 * @param <T> the type of objects to be broadcasted
 */
public class AParamWSHandler<A extends AParamWSAdapter<T>, T> {
	
	private Map<String, List<A>> connectedWS = new ConcurrentHashMap<String, List<A>>();
	
	
	/**
	 * @param name the name for which the WS adapter should be added
	 * @param adapter the WS adapter to add
	 */
	public void addSocketAdapter(String name, A adapter) {
		if (this.connectedWS.containsKey(name)) {
			List<A> list = this.connectedWS.get(name);
			list.add(adapter);
		} else {
			List<A> newList = new ArrayList<>();
			newList.add(adapter);
			this.connectedWS.put(name, newList);
		}
	}
	
	/**
	 * @param name the name for which the given adapter should be removed
	 * @param adapter the adapter to be removed
	 * @return true if removal was successful, false otherwise
	 */
	public boolean removeSocketAdapter(String name, A adapter) {
		if (this.connectedWS.containsKey(name)) {
			List<A> list = this.connectedWS.get(name);
			return list.remove(adapter);
		}
		
		return false;
	}
	
	/**
	 * @param name the name for which the event should be broadcasted
	 * @param event the event to be broadcasted
	 */
	public void broadcastChange(String name, WSChangeEvent<T> event) {
		List<A> adapters = this.connectedWS.get(name);
		
		if ((adapters == null) || adapters.isEmpty()) {
			return;
		}
		
		for (A adapter : adapters) {
			adapter.sendChangeEvent(event);
		}
	}
	
}
