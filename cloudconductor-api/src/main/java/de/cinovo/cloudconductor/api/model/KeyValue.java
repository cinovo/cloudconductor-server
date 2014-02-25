package de.cinovo.cloudconductor.api.model;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class KeyValue implements Comparable<KeyValue> {
	
	private String key;
	
	private Object value;
	
	
	/**
	 * @param key the key
	 * @param value the value
	 */
	@JsonCreator
	public KeyValue(@JsonProperty("key") String key, @JsonProperty("value") Object value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(KeyValue o) {
		return this.key.compareTo(o.getKey());
	}
	
}
