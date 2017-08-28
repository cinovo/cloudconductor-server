package de.cinovo.cloudconductor.server.handler;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.ETemplate;

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
	
	@Autowired
	private ITemplateDAO templateDao;
	
	
	/**
	 * @param entityKey the existing ssh key entity
	 * @param updatedSSHKey the updated ssh key
	 * @return the updated ssh key entity
	 */
	public ESSHKey updateEntity(ESSHKey entityKey, SSHKey updatedSSHKey) {
		this.fillFields(entityKey, updatedSSHKey);
		return this.sshKeyDao.save(entityKey);
	}
	
	/**
	 * @param newSSHKey the ssh key to create
	 * @return the new created ssh key entity
	 */
	public ESSHKey createEntity(SSHKey newSSHKey) {
		ESSHKey entityKey = new ESSHKey();
		this.fillFields(entityKey, newSSHKey);
		return this.sshKeyDao.save(entityKey);
	}
	
	private void fillFields(ESSHKey entityKey, SSHKey newSSHKey) {
		entityKey.setOwner(newSSHKey.getOwner());
		entityKey.setKeycontent(newSSHKey.getKey());
		entityKey.setUsername(newSSHKey.getUsername());
		entityKey.setLastChangedDate(new Date().getTime());
		
		entityKey.setTemplates(new ArrayList<ETemplate>());
		for (ETemplate template : this.templateDao.findList()) {
			if (newSSHKey.getTemplates().contains(template.getName())) {
				entityKey.getTemplates().add(template);
			}
		}
	}
}
