/**
 * 
 */
package de.cinovo.cloudconductor.server.rest.filter;

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

import org.restdoc.api.GlobalHeader;
import org.restdoc.cxf.RestDocFeature;

import de.cinovo.cloudconductor.api.interfaces.IAgent;
import de.cinovo.cloudconductor.api.interfaces.IConfigValue;
import de.cinovo.cloudconductor.api.interfaces.IFile;
import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.interfaces.IIOModule;
import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.interfaces.ISSHKey;
import de.cinovo.cloudconductor.api.interfaces.IService;
import de.cinovo.cloudconductor.api.interfaces.ITemplate;

/**
 * 
 * Copyright 2012 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
public class RestDocFilter extends RestDocFeature {
	
	@Override
	protected String getBaseURL() {
		return "/api";
	}
	
	@Override
	protected Class<?>[] getClasses() {
		final Class<?>[] classes = {IAgent.class, IFile.class, ITemplate.class, IHost.class, IPackage.class, IService.class, ISSHKey.class, IConfigValue.class, IIOModule.class};
		return classes;
	}
	
	@Override
	protected GlobalHeader getHeader() {
		return new GlobalHeader();
	}
}
