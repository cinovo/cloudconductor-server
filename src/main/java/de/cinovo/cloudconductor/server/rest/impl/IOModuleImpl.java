package de.cinovo.cloudconductor.server.rest.impl;

/*
 * #%L cloudconductor-server %% Copyright (C) 2013 - 2014 Cinovo AG %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IIOModule;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.util.IPackageImport;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@JaxRsComponent
public class IOModuleImpl extends ImplHelper implements IIOModule {
	
	@Autowired
	private IPackageImport importer;
	
	
	@Override
	public Response importVersions(Set<PackageVersion> versions) {
		Map<String, Set<PackageVersion>> groupMap = new HashMap<>();
		for (PackageVersion packageVersion : versions) {
			if (!groupMap.containsKey(packageVersion.getPackageServerGroup())) {
				groupMap.put(packageVersion.getPackageServerGroup(), new HashSet<PackageVersion>());
			}
			groupMap.get(packageVersion.getPackageServerGroup()).add(packageVersion);
		}
		
		for (Entry<String, Set<PackageVersion>> entry : groupMap.entrySet()) {
			this.importer.importVersions(entry.getValue(), entry.getKey());
		}
		return Response.ok().build();
	}
	
}
