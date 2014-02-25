package de.cinovo.cloudconductor.api.interfaces;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.PackageVersion;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path(IRestPath.IO)
public interface IIOModule {
	
	/**
	 * Batch import versions of packages into cloudconductor.<br>
	 * Also cleans up the package versions of the existing packages.<br>
	 * Used mainly for import of provided versions within yum (see <i>cloudconductor/package/helper/syncYum.sh</i>)
	 * 
	 * @param versions the versions of a package to add
	 * @return a response
	 */
	@POST
	@Path(IRestPath.IO_VERSION)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response importVersions(Set<PackageVersion> versions);
}
