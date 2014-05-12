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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IWebPath.DEFAULTVIEW)
public interface IIndex {
	
	/**
	 * @return the main page
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract ViewModel view();
	
	/**
	 * Returns the css style used in the web pages.
	 * 
	 * @param css the file name
	 * @return the css style used in the web pages
	 */
	@GET
	@Path("css/{css}")
	@Produces(MediaType.TEXT_CSS)
	public abstract InputStream getCSS(@PathParam("css") String css);
	
	/**
	 * @param css the file name
	 * @return the bootstrap css style
	 */
	@GET
	@Path("bootstrap/css/{css}")
	@Produces(MediaType.TEXT_CSS)
	public abstract InputStream getBSCSS(@PathParam("css") String css);
	
	/**
	 * Returns the CSS style used in the web pages.
	 * 
	 * @param img the img name
	 * @return the CSS style used in the web pages
	 */
	@GET
	@Path("images/{img}")
	@Produces(MediaType.IMAGE_GIF)
	public abstract InputStream getImage(@PathParam("img") String img);
	
	/**
	 * Returns the java script files
	 * 
	 * @param js the file name
	 * @return the java script files
	 */
	@GET
	@Path("js/{js}")
	@Produces(MediaType.APPLICATION_JAVASCRIPT)
	public abstract InputStream getJS(@PathParam("js") String js);
	
	/**
	 * @param js the file name
	 * @return the bootstrap java script files
	 */
	@GET
	@Path("bootstrap/js/{js}")
	@Produces(MediaType.APPLICATION_JAVASCRIPT)
	public abstract InputStream getBSJS(@PathParam("js") String js);
	
	/**
	 * @param font the file name
	 * @return the bootstrap font files
	 */
	@GET
	@Path("bootstrap/fonts/{font}")
	@Produces(MediaType.FONT_OPENTYPE)
	public abstract InputStream getBSFonts(@PathParam("font") String font);
	
	/**
	 * @return an ajax answer
	 */
	@POST
	@Path("autorefresh/toggle")
	@Produces(MediaType.APPLICATION_JSON)
	public abstract AjaxAnswer toggleAutoRefresh();
	
}
