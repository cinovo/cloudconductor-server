package de.cinovo.cloudconductor.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * Allows to store additional java properties exported to the client
 * 
 * @author Stephan
 * 
 */
public class AdditionalJavaPropsStore {
	
	private static final Map<String, String> propertyMap = new ConcurrentHashMap<>();
	
	
	/**
	 * @param key name of the property
	 * @param value value of the property
	 */
	public static synchronized void addProperty(String key, String value) {
		if (AdditionalJavaPropsStore.propertyMap.containsKey(key)) {
			throw new RuntimeException("Trying to store a property twice: " + key);
		}
		AdditionalJavaPropsStore.propertyMap.put(key, value);
	}
	
	/**
	 * @param sourceMap sourceMap to merge with
	 * @return a new merged map
	 */
	public static synchronized Map<String, String> merge(Map<String, String> sourceMap) {
		Map<String, String> result = new HashMap<>(AdditionalJavaPropsStore.propertyMap);
		result.putAll(sourceMap);
		return result;
	}
	
	/**
	 * @param key the key
	 * @return the value of the key, null if key does not exist
	 */
	public static synchronized String getValue(String key) {
		return AdditionalJavaPropsStore.propertyMap.get(key);
	}
}
