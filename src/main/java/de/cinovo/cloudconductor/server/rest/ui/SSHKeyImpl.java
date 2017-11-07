package de.cinovo.cloudconductor.server.rest.ui;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SSHKeyImpl.class);
	
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
		RESTAssert.assertNotNull(modelKey, Response.Status.NOT_FOUND);
		return modelKey.toApi();
	}
	
	@Override
	@Transactional
	public void saveKey(SSHKey key) {
		RESTAssert.assertNotNull(key);
		RESTAssert.assertNotEmpty(key.getOwner());
		RESTAssert.assertNotEmpty(key.getUsername());
		RESTAssert.assertNotEmpty(key.getKey());
		
		ESSHKey eKey = this.sshDAO.findByName(key.getOwner());
		if (eKey == null) {
			this.sshHandler.createEntity(key);
		} else {
			this.sshHandler.updateEntity(eKey, key);
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
