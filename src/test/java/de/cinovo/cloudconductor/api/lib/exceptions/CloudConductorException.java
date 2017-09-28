package de.cinovo.cloudconductor.api.lib.exceptions;

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

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Exception for problems related to the CloudConductor.
 * 
 * @author mhilbert
 */
public class CloudConductorException extends Exception {
	
	/** version UID for serialization */
	private static final long serialVersionUID = 1L;
	
	/** the HTTP status code for the problem (may not be set) */
	private Integer statusCode = null;
	
	
	/**
	 * Class constructor.
	 */
	public CloudConductorException() {
		// Do nothing.
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param message the detail message
	 */
	public CloudConductorException(String message) {
		super(message);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param statusCode the status code
	 * @param message the detail message
	 */
	public CloudConductorException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param message the detail message
	 * @param cause the cause
	 */
	public CloudConductorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param statusCode the status code
	 * @param message the detail message
	 * @param cause the cause
	 */
	public CloudConductorException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}
	
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return this.statusCode;
	}
	
}
