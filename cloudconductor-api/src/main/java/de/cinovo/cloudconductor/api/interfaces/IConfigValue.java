package de.cinovo.cloudconductor.api.interfaces;

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

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.KeyValue;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.CONFIG)
public interface IConfigValue {
	
	/**
	 * Returns configuration of the given template as Key-Value Pairs
	 * 
	 * @param template the template name
	 * @return map of key value pairs representing the configuration of the template
	 */
	@GET
	@Path(IRestPath.CONFIG_TEMPLATE)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVAARGS, MediaType.APPLICATION_JAVAPROPS})
	public Map<String, String> get(@PathParam(IRestPath.VAR_TEMPLATE) String template);
	
	/**
	 * Returns configuration of the given service within the template as Key-Value Pairs
	 * 
	 * @param template the template name
	 * @param service the name of the service
	 * @return map of key value pairs representing the configuration of the service within the template
	 */
	@GET
	@Path(IRestPath.CONFIG_TEMPLATE_SERVICE)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVAARGS, MediaType.APPLICATION_JAVAPROPS})
	public Map<String, String> get(@PathParam(IRestPath.VAR_TEMPLATE) String template, @PathParam(IRestPath.VAR_SERVICE) String service);
	
	/**
	 * Returns the value for a key of the given service within the template as Key-Value Pairs
	 * 
	 * @param template the template name
	 * @param service the name of the service
	 * @param key the name of the key
	 * @return the value of the key of the service within the template
	 */
	@GET
	@Path(IRestPath.CONFIG_TEMPLATE_SERVICE_KEY)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVAARGS, MediaType.APPLICATION_JAVAPROPS})
	public String get(@PathParam(IRestPath.VAR_TEMPLATE) String template, @PathParam(IRestPath.VAR_SERVICE) String service, @PathParam(IRestPath.VAR_KEY) String key);
	
	/**
	 * Adds a new key-value pair to the configuration of a service within a template
	 * 
	 * @param template the template name
	 * @param service the name of the service
	 * @param pair the key value pair
	 */
	@PUT
	@Path(IRestPath.CONFIG_TEMPLATE_SERVICE)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@PathParam(IRestPath.VAR_TEMPLATE) String template, @PathParam(IRestPath.VAR_SERVICE) String service, KeyValue pair);
	
	/**
	 * Adds a new key-value pair to the configuration of a template
	 * 
	 * @param template the template name
	 * @param pair the key value pair
	 */
	@PUT
	@Path(IRestPath.CONFIG_TEMPLATE)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@PathParam(IRestPath.VAR_TEMPLATE) String template, KeyValue pair);
	
	/**
	 * Delete a key-value pair from the configuration of a template
	 * 
	 * @param template the template name
	 * @param key the key
	 */
	@DELETE
	@Path(IRestPath.CONFIG_TEMPLATE_KEY)
	public void delete(@PathParam(IRestPath.VAR_TEMPLATE) String template, @PathParam(IRestPath.VAR_KEY) String key);
	
	/**
	 * Delete a key-value pair from the configuration of a service within a template
	 * 
	 * @param template the template name
	 * @param service the name of the service
	 * @param key the key
	 */
	@DELETE
	@Path(IRestPath.CONFIG_TEMPLATE_SERVICE_KEY)
	public void delete(@PathParam(IRestPath.VAR_TEMPLATE) String template, @PathParam(IRestPath.VAR_SERVICE) String service, @PathParam(IRestPath.VAR_KEY) String key);
	
}
