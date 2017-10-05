package de.cinovo.cloudconductor.server.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class SSHHandler {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SSHHandler.class);
	
	@Autowired
	private ISSHKeyDAO sshKeyDao;
	
	@Autowired
	private ITemplateDAO templateDao;
	
	
	/**
	 * @param templateName the name of the template
	 * @return set of SSH keys for the given template
	 */
	public Set<SSHKey> getSSHKeyForTemplate(String templateName) {
		ETemplate template = this.templateDao.findByName(templateName);
		RESTAssert.assertNotNull(template);
		
		Set<SSHKey> result = new HashSet<>();
		for (ESSHKey key : template.getSshkeys()) {
			result.add(key.toApi());
		}
		
		return result;
	}
	
	/**
	 * @param entityKey the existing ssh key entity
	 * @param updatedSSHKey the updated ssh key
	 * @return the updated ssh key entity
	 */
	public ESSHKey updateEntity(ESSHKey entityKey, SSHKey updatedSSHKey) {
		SSHHandler.LOGGER.info("Update existing SSH key of '" + updatedSSHKey.getOwner() + "'...");
		this.fillFields(entityKey, updatedSSHKey);
		return this.sshKeyDao.save(entityKey);
	}
	
	/**
	 * @param newSSHKey the ssh key to create
	 * @return the new created ssh key entity
	 */
	public ESSHKey createEntity(SSHKey newSSHKey) {
		SSHHandler.LOGGER.info("Create new SSH key for '" + newSSHKey.getOwner() + "'...");
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
