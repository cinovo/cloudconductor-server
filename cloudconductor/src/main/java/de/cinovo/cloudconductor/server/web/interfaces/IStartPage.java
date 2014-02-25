package de.cinovo.cloudconductor.server.web.interfaces;

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

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IWebPath.DEFAULTVIEW)
public interface IStartPage {
	
	/**
	 * @return the main page with links
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	/**
	 * Returns the CSS style used in the web pages.
	 * 
	 * @return the CSS style used in the web pages
	 */
	@GET
	@Path("/style.css")
	@Produces(MediaType.TEXT_CSS)
	public abstract InputStream getStyle();
	
	/**
	 * Returns the CSS style used in the web pages.
	 * 
	 * @param img the img name
	 * 
	 * @return the CSS style used in the web pages
	 */
	@GET
	@Path("/images/{img}")
	@Produces(MediaType.IMAGE_GIF)
	public abstract InputStream getImage(@PathParam("img") String img);
	
}
