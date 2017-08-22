package de.cinovo.cloudconductor.server.rest.ui;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.ISSHKey;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class SSHKeyImpl implements ISSHKey {
	
	@Autowired
	private ISSHKeyDAO sshDAO;
	
	@Autowired
	private SSHHandler sshHandler;
	
	
	@Override
	@Transactional
	public SSHKey[] getKeys() {
		Set<SSHKey> result = new HashSet<>();
		for (ESSHKey sshKey : this.sshDAO.findList()) {
			result.add(sshKey.toApi());
		}
		
		return result.toArray(new SSHKey[result.size()]);
	}
	
	@Override
	@Transactional
	public SSHKey getKey(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey modelKey = this.sshDAO.findByName(owner);
		RESTAssert.assertNotNull(modelKey);
		return modelKey.toApi();
	}
	
	@Override
	@Transactional
	public void saveKey(SSHKey key) {
		RESTAssert.assertNotNull(key);
		ESSHKey modelKey = this.sshDAO.findByName(key.getOwner());
		
		if (modelKey == null) {
			this.sshHandler.createEntity(key);
		} else {
			this.sshHandler.updateEntity(key);
		}
	}
	
	@Override
	@Transactional
	public void deleteKey(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey model = this.sshDAO.findByName(owner);
		RESTAssert.assertNotNull(model);
		this.sshDAO.delete(model);
	}
	
}
