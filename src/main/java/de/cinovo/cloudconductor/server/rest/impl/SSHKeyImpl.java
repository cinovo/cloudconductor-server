package de.cinovo.cloudconductor.server.rest.impl;

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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.ISSHKey;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@JaxRsComponent
public class SSHKeyImpl extends ImplHelper implements ISSHKey {
	
	@Autowired
	private ISSHKeyDAO dsshkey;
	@Autowired
	private AMConverter amc;
	
	
	@Override
	@Transactional
	public SSHKey[] get() {
		Set<SSHKey> result = new HashSet<>();
		for (ESSHKey t : this.dsshkey.findList()) {
			result.add(MAConverter.fromModel(t));
		}
		return result.toArray(new SSHKey[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(String name, SSHKey sshKey) {
		this.assertName(name, sshKey);
		ESSHKey key = this.amc.toModel(sshKey);
		this.dsshkey.save(key);
	}
	
	@Override
	@Transactional
	public SSHKey get(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dsshkey.findByOwner(owner);
		this.assertModelFound(key);
		return MAConverter.fromModel(key);
	}
	
	@Override
	@Transactional
	public void delete(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey key = this.dsshkey.findByOwner(owner);
		this.assertModelFound(key);
		this.dsshkey.delete(key);
	}
	
}
