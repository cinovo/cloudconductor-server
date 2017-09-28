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
 * Exception for server errors.
 * 
 * @author mhilbert
 */
public class ServerErrorException extends CloudConductorException {
	
	/** version UID for serialization */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Class constructor.
	 */
	public ServerErrorException() {
		super();
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param statusCode the status code
	 * @param message the detail message
	 * @param cause the cause
	 */
	public ServerErrorException(int statusCode, String message, Throwable cause) {
		super(statusCode, message, cause);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param statusCode the status code
	 * @param message the detail message
	 */
	public ServerErrorException(int statusCode, String message) {
		super(statusCode, message);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param message the detail message
	 * @param cause the cause
	 */
	public ServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param message the detail message
	 */
	public ServerErrorException(String message) {
		super(message);
	}
	
}
