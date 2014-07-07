package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.RenderedView;

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

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author astifel
 * 
 */
@Path(IAudit.ROOT)
public interface IAudit {
	
	/** the root */
	public static final String ROOT = "/audit";
	/***/
	public static final String RESERVED_AUDIT = "AUDIT";
	
	
	/**
	 * @param range the audit-date-range
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	public abstract RenderedView view(@QueryParam("daterange") String range);
	
}
