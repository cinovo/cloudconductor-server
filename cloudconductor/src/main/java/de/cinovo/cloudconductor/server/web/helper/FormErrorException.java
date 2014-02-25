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

import javax.ws.rs.core.MultivaluedMap;

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
	private MultivaluedMap<String, String> formParams;
	
	
	/**
	 * @param parentUrl the parent url to redirect to
	 * @param msg the error message
	 */
	public FormErrorException(String parentUrl, String msg) {
		super(msg);
		this.parentUrl = parentUrl;
	}
	
	/**
	 * @param parentUrl the parent url to redirect to
	 * @param msg the error message
	 * @param formParams parameters from a form
	 */
	public FormErrorException(String parentUrl, String msg, MultivaluedMap<String, String> formParams) {
		super(msg);
		this.parentUrl = parentUrl;
		this.formParams = formParams;
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
	public MultivaluedMap<String, String> getFormParams() {
		return this.formParams;
	}
	
}
