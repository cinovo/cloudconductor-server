package de.cinovo.cloudconductor.server.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.cinovo.cloudconductor.server.dao.hibernate.ConfigValueDAOHib;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *         <p>
 *         REVIEW: BETTER SOLUTION?
 */
public class ReservedConfigKeyStore {
	
	/**
	 * the ReservedConfigKeyStore singleton
	 */
	public static ReservedConfigKeyStore instance = new ReservedConfigKeyStore();
	
	private Map<String, String> reserved = new HashMap<>();
	
	
	private ReservedConfigKeyStore() {
		this.initPropertyValues();
	}
	
	private void initPropertyValues() {
		this.registerReserverdKey(ICCProperties.CC_NAME, (String) System.getProperties().get(ICCProperties.CC_NAME));
	}
	
	/**
	 * @param key the new key to register
	 * @param value the value of the key
	 */
	public void registerReserverdKey(String key, String value) {
		if (this.reserved.get(key) == null) {
			this.reserved.put(key, value);
		}
	}
	
	/**
	 * @param key the key to check
	 * @return existing or not
	 */
	public boolean isReserved(String key) {
		return this.reserved.containsKey(key);
	}
	
	/**
	 * @param key the key
	 * @return the value
	 */
	public String getValue(String key) {
		return this.reserved.get(key);
	}
	
	/**
	 * @return the map containing keys and values
	 */
	public Collection<ConfigValue> getReservedAsConfigValue() {
		Set<ConfigValue> result = new HashSet<>();
		for (Entry<String, String> entry : this.reserved.entrySet()) {
			if ((entry.getValue() != null) && !entry.getValue().isEmpty()) {
				ConfigValue configValue = new ConfigValue();
				configValue.setKey(entry.getKey());
				configValue.setTemplate(ConfigValueDAOHib.RESERVED_GLOBAL);
				configValue.setValue(entry.getValue());
				result.add(configValue);
			}
		}
		return result;
	}
}
