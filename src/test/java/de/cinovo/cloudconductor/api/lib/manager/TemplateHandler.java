package de.cinovo.cloudconductor.api.lib.manager;

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

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;

import java.util.Set;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class TemplateHandler extends DefaultRestHandler<Template> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public TemplateHandler(String cloudconductorUrl, String token) {
		super(cloudconductorUrl, token);
	}
	

	@Override
	protected String getDefaultPath() {
		return "/template";
	}
	
	@Override
	protected Class<Template> getAPIClass() {
		return Template.class;
	}
	
	/**
	 * @param template the template name
	 * @return set of hosts using the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<Host> getHosts(String template) throws CloudConductorException {
		String path = this.pathGenerator("/{template}/hosts", template);
		return (Set<Host>) this._get(path, this.getSetType(Host.class));
	}
	
	/**

	 * @param template the template name
	 * @return set of sshkeys used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<SSHKey> getSSHKeys(String template) throws CloudConductorException {
		String path = this.pathGenerator("/{template}/sshkeys", template);
		return (Set<SSHKey>) this._get(path, this.getSetType(SSHKey.class));
	}

	/**
	 * @param template the template name
	 * @return set of package versions used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<PackageVersion> getVersions(String template) throws CloudConductorException {
		String path = this.pathGenerator("/{template}/package/versions", template);
		return (Set<PackageVersion>) this._get(path, this.getSetType(PackageVersion.class));
	}

	/**
	 * @param template the template name
	 * @return set of services used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@SuppressWarnings("unchecked")
	public Set<Service> getServices(String template) throws CloudConductorException {
		String path = this.pathGenerator("/{template}/services", template);
		return (Set<Service>) this._get(path, this.getSetType(Service.class));
	}
	
}
