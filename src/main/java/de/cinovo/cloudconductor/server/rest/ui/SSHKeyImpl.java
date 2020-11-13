package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.ISSHKey;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
	@Autowired
	private ITemplateDAO templateDAO;
	
	@Override
	@Transactional
	public SSHKey[] getKeys() {
		return this.sshDAO.findList().stream().map(k->k.toApi(this.templateDAO)).distinct().toArray(SSHKey[]::new);
	}
	
	@Override
	@Transactional
	public SSHKey getKey(String owner) {
		RESTAssert.assertNotEmpty(owner);
		ESSHKey modelKey = this.sshDAO.findByName(owner);
		RESTAssert.assertNotNull(modelKey, Response.Status.NOT_FOUND);
		return modelKey.toApi(this.templateDAO);
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
		RESTAssert.assertNotNull(model, Status.NOT_FOUND);
		this.sshDAO.delete(model);
	}
	
}
