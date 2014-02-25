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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.TEMPLATE)
public interface ITemplate extends IDefaultApi<Template> {
	
	/**
	 * @param name the template name
	 * @return collection of services available on the template
	 */
	@GET
	@Path(IRestPath.TEMPLATE_SERVICE)
	@Produces(MediaType.APPLICATION_JSON)
	public Service[] getServices(@PathParam(IRestPath.VAR_TEMPLATE) String name);
	
	/**
	 * @param name the template name
	 * @return collection of hosts available on the template
	 */
	@GET
	@Path(IRestPath.TEMPLATE_HOST)
	@Produces(MediaType.APPLICATION_JSON)
	public Host[] getHosts(@PathParam(IRestPath.VAR_TEMPLATE) String name);
	
	/**
	 * Add/Modify a host to the template
	 * 
	 * @param name the template name
	 * @param host the host name
	 */
	@PUT
	@Path(IRestPath.TEMPLATE_HOST_SINGLE)
	public void addHost(@PathParam(IRestPath.VAR_TEMPLATE) String name, @PathParam(IRestPath.VAR_HOST) String host);
	
	/**
	 * Delete a host from the template
	 * 
	 * @param name the template name
	 * @param host the host name
	 */
	@DELETE
	@Path(IRestPath.TEMPLATE_HOST_SINGLE)
	public void removeHost(@PathParam(IRestPath.VAR_TEMPLATE) String name, @PathParam(IRestPath.VAR_HOST) String host);
	
	/**
	 * @param name the template name
	 * @return collection of ssh keys available on the template
	 */
	@GET
	@Path(IRestPath.TEMPLATE_SSHKEY)
	@Produces(MediaType.APPLICATION_JSON)
	public SSHKey[] getSSHKeys(@PathParam(IRestPath.VAR_TEMPLATE) String name);
	
	/**
	 * Add/Modify a ssh key to the template
	 * 
	 * @param name the template name
	 * @param key the key name
	 */
	@PUT
	@Path(IRestPath.TEMPLATE_SSHKEY_SINGLE)
	public void addSSHKey(@PathParam(IRestPath.VAR_TEMPLATE) String name, @PathParam(IRestPath.VAR_NAME) String key);
	
	/**
	 * Delete a ssh key from the template
	 * 
	 * @param name the template name
	 * @param key the key
	 */
	@DELETE
	@Path(IRestPath.TEMPLATE_SSHKEY_SINGLE)
	public void removeSSHKey(@PathParam(IRestPath.VAR_TEMPLATE) String name, @PathParam(IRestPath.VAR_NAME) String key);
	
	/**
	 * @param name the template name
	 * @return collection of package versions available on the template
	 */
	@GET
	@Path(IRestPath.TEMPLATE_VERSION)
	@Produces(MediaType.APPLICATION_JSON)
	public PackageVersion[] getRPMS(@PathParam(IRestPath.VAR_TEMPLATE) String name);
	
	/**
	 * Add/Modify a package version to the template
	 * 
	 * @param name the template name
	 * @param rpm the package version
	 */
	@POST
	@Path(IRestPath.TEMPLATE_VERSION)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addRPM(@PathParam(IRestPath.VAR_TEMPLATE) String name, PackageVersion rpm);
	
	/**
	 * Delete a package version from the template
	 * 
	 * @param name the template name
	 * @param pkg the package name
	 * @param version the package version
	 */
	@DELETE
	@Path(IRestPath.TEMPLATE_VERSION_SINGLE)
	public void removeRPM(@PathParam(IRestPath.VAR_TEMPLATE) String name, @PathParam(IRestPath.VAR_PKG) String pkg, @PathParam(IRestPath.VAR_VERSION) String version);
	
}
