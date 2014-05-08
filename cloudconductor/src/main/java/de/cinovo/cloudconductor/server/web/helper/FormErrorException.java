package de.cinovo.cloudconductor.server.web.helper;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class FormErrorException extends Exception implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String parentUrl;
	
	
	/**
	 * @param parts the parentUrl to set
	 */
	public void setParentUrl(String... parts) {
		StringBuilder b = new StringBuilder();
		for (String p : parts) {
			if (!p.startsWith("/")) {
				b.append("/");
			}
			b.append(p);
		}
		this.parentUrl = b.toString();
	}
	
	
	private Map<String, Object> formParams = new HashMap<>();
	
	private Set<String> failedElements = new HashSet<>();
	
	
	/**
	 * @param parentUrl the parent url to redirect to
	 * @param msg the error message
	 */
	public FormErrorException(String parentUrl, String msg) {
		super(msg);
		this.parentUrl = parentUrl;
	}
	
	/**
	 * @return the parentUrl
	 */
	public String getParentUrl() {
		return this.parentUrl;
	}
	
	/**
	 * @return the formParams
	 */
	public Map<String, Object> getFormParams() {
		return this.formParams;
	}
	
	/**
	 * @param elementName the form element name
	 * @param value the value
	 */
	public void addFormParam(String elementName, Object value) {
		this.formParams.put(elementName, value);
	}
	
	/**
	 * @param elementName the form element name
	 * @param value the value
	 * @param failed set if element has error
	 */
	public void addFormParam(String elementName, Object value, boolean failed) {
		this.formParams.put(elementName, value);
		if (failed) {
			this.failedElements.add(elementName);
		}
	}
	
	public void addElementError(String elementName, boolean failed) {
		if (failed) {
			this.failedElements.add(elementName);
		}
	}
	
	public boolean hasError(String elementName) {
		return this.failedElements.contains(elementName);
	}
}
