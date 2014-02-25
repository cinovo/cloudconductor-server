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

import de.cinovo.cloudconductor.api.IRestPath;
import de.cinovo.cloudconductor.api.lib.helper.DefaultRestHandler;
import de.cinovo.cloudconductor.api.model.SSHKey;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class SSHKeyHandler extends DefaultRestHandler<SSHKey> {
	
	/**
	 * @param cloudconductorUrl the config server url
	 */
	public SSHKeyHandler(String cloudconductorUrl) {
		super(cloudconductorUrl);
	}
	
	@Override
	protected String getDefaultPath() {
		return IRestPath.SSHKEY;
	}
	
	@Override
	protected Class<SSHKey> getAPIClass() {
		return SSHKey.class;
	}
	
}
