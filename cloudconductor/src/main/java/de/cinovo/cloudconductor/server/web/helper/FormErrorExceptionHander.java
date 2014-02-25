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

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.MessageContext;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Provider
public class FormErrorExceptionHander implements ExceptionMapper<FormErrorException> {
	
	/**
	 * identifier for an error message
	 */
	public static final String FORM_ERROR_MESSAGE = "FORM_ERROR_MESSAGE";
	/**
	 * identifier for error data from a form
	 */
	public static final String FORM_ERROR_DATA = "FORM_ERROR_DATA";
	
	@Context
	protected MessageContext mc;
	
	
	@Override
	public Response toResponse(FormErrorException exception) {
		ResponseBuilder seeOther;
		try {
			seeOther = Response.seeOther(new URI(exception.getParentUrl()));
		} catch (URISyntaxException e) {
			return Response.serverError().build();
		}
		this.mc.getHttpServletRequest().getSession(true).setAttribute(FormErrorExceptionHander.FORM_ERROR_MESSAGE, exception.getMessage());
		this.mc.getHttpServletRequest().getSession(true).setAttribute(FormErrorExceptionHander.FORM_ERROR_DATA, exception.getFormParams());
		return seeOther.build();
	}
}
