package de.cinovo.cloudconductor.server.web;

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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Custom message body provider for the web templates.
 * 
 * @author psigloch
 */
@Provider
public class TemplateWriter implements MessageBodyWriter<RenderedView> {
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return RenderedView.class.isAssignableFrom(type) && mediaType.getSubtype().equals("html");
	}
	
	@Override
	public long getSize(RenderedView t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return t.getContent().length();
	}
	
	@Override
	public void writeTo(RenderedView t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		entityStream.write(t.getContent().getBytes("UTF-8"));
	}
	
}
