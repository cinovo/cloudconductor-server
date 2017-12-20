package de.cinovo.cloudconductor.server.rest.utils;

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

import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@Provider
@Produces(de.cinovo.cloudconductor.api.MediaType.APPLICATION_JAVAPROPS)
@JaxRsComponent
public class JavaPropsWriter implements MessageBodyWriter<ConfigValue[]> {
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return mediaType.getSubtype().equals("x-javaprops");
	}
	
	@Override
	public long getSize(ConfigValue[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}
	
	@Override
	public void writeTo(ConfigValue[] t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		final PrintStream printStream = new PrintStream(entityStream, false, "UTF8");
		for (ConfigValue entry : t) {
			printStream.println(entry.getKey() + " = " + entry.getValue());
		}
		printStream.flush();
		printStream.close();
	}
	
}
