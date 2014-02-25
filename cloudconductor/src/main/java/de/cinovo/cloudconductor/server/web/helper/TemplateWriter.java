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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import de.taimos.cxf_renderer.velocity.VelocityBodyWriter;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Custom message body provider for the web templates.
 * 
 * @author mhilbert/psigloch
 */
@Provider
public class TemplateWriter extends VelocityBodyWriter {
	
	@Override
	protected String generateTemplateName(final String viewName, final MediaType mediaType) {
		return "/templates/includes/index.vm";
	}
	
	@Override
	protected List<MediaType> getMediaTypes() {
		final List<MediaType> r = new ArrayList<>(super.getMediaTypes());
		r.add(MediaType.valueOf(de.cinovo.cloudconductor.api.MediaType.TEXT_HTML));
		return r;
	}
	
}
