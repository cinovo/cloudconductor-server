package de.cinovo.cloudconductor.server.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class SSHHandler {
	
	@Autowired
	private ISSHKeyDAO sshKeyDao;
	
	
	/**
	 * @param newSSHKey the new ssh key to create
	 * @return the new created ssh key entity
	 */
	public ESSHKey createEntity(SSHKey newSSHKey) {
		// TODO not implemented yet!
		return null;
	}
	
	/**
	 * @param updatedSSHKey the updated ssh key
	 * @return the updated ssh key entity
	 */
	public ESSHKey updateEntity(SSHKey updatedSSHKey) {
		// TODO not implemented yet!
		return null;
	}
}
