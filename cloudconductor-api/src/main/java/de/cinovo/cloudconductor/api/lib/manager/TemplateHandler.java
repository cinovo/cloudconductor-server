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

import java.util.Set;

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
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
public class TemplateHandler extends DefaultRestHandler<Template> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public TemplateHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	@Override
	protected String getDefaultPath() {
		return IRestPath.TEMPLATE;
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
	public Set<Host> getHosts(String template) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_HOST, template);
		return (Set<Host>) this._get(path, this.getSetType(Host.class));
	}
	
	/**
	 * DOES NOT MAKE SENSE??
	 * 
	 * @param template the template name
	 * @param host the host name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@Deprecated
	public void addHost(String template, String host) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_HOST_SINGLE, template, host);
		this._put(path);
	}
	
	/**
	 * DOES NOT MAKE SENSE??
	 * 
	 * @param template the template name
	 * @param host the host name
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	@Deprecated
	public void removeHost(String template, String host) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_HOST_SINGLE, template, host);
		this._delete(path);
	}
	
	/**
	 * @param template the template name
	 * @return set of sshkeys used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Set<SSHKey> getSSHKeys(String template) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_SSHKEY, template);
		return (Set<SSHKey>) this._get(path, this.getSetType(SSHKey.class));
	}
	
	/**
	 * @param template the template name
	 * @param key the sshkey identifier
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addSSHKey(String template, String key) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_SSHKEY_SINGLE, template, key);
		this._put(path);
	}
	
	/**
	 * @param template the template name
	 * @param key the sshkey identifier
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removeSSHKey(String template, String key) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_SSHKEY_SINGLE, template, key);
		this._delete(path);
	}
	
	/**
	 * @param template the template name
	 * @return set of package versions used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Set<PackageVersion> getVersions(String template) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_VERSION, template);
		return (Set<PackageVersion>) this._get(path, this.getSetType(PackageVersion.class));
	}
	
	/**
	 * @param template the template name
	 * @param version the package version identifier
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void addVersion(String template, PackageVersion version) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_VERSION, template);
		this._post(path, version);
	}
	
	/**
	 * @param template the template name
	 * @param version the package version identifier
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public void removeVersion(String template, PackageVersion version) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_VERSION_SINGLE, template, version.getName(), version.getVersion());
		this._delete(path);
	}
	
	/**
	 * @param template the template name
	 * @return set of services used by the template
	 * @throws CloudConductorException Error indicating connection or data problems
	 */
	public Set<Service> getServices(String template) throws CloudConductorException {
		String path = this.pathGenerator(IRestPath.TEMPLATE_SERVICE, template);
		return (Set<Service>) this._get(path, this.getSetType(Service.class));
	}
	
}
