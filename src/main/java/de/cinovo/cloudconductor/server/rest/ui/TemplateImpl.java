package de.cinovo.cloudconductor.server.rest.ui;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.template.TemplateDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.template.TemplatesWSHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class TemplateImpl implements ITemplate {
	
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private TemplateHandler templateHandler;
	@Autowired
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private TemplatesWSHandler templatesWSHandler;
	@Autowired
	private TemplateDetailWSHandler templateDetailWSHandler;
	@Autowired
	private SSHHandler sshKeyHandler;
	
	
	@Override
	@Transactional
	public Set<Template> get() {
		Set<Template> result = new HashSet<>();
		for (ETemplate template : this.templateDAO.findList()) {
			result.add(template.toApi());
		}
		return result;
	}
	
	@Override
	@Transactional
	public void save(Template template) {
		RESTAssert.assertNotNull(template);
		RESTAssert.assertNotEmpty(template.getName());
		ETemplate eTemplate = this.templateDAO.findByName(template.getName());
		if (eTemplate == null) {
			eTemplate = this.templateHandler.createEntity(template);
			this.templatesWSHandler.broadcastEvent(new WSChangeEvent<Template>(ChangeType.ADDED, eTemplate.toApi()));
		} else {
			eTemplate = this.templateHandler.updateEntity(eTemplate, template);
			Template aTemplate = eTemplate.toApi();
			this.templatesWSHandler.broadcastEvent(new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
			this.templateDetailWSHandler.broadcastChange(eTemplate.getName(), new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
		}
	}
	
	@Override
	@Transactional
	public void delete(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate eTemplate = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(eTemplate);
		this.templateDAO.delete(eTemplate);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<Template>(ChangeType.DELETED, eTemplate.toApi()));
	}
	
	@Override
	@Transactional
	public Template get(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template, Status.NOT_FOUND);
		return template.toApi();
	}
	
	@Override
	@Transactional
	public Template updatePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		template = this.templateHandler.updatePackage(template, packageName);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi();
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
		return aTemplate;
	}
	
	@Override
	@Transactional
	public Template deletePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		template = this.templateHandler.removePackage(template, packageName);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi();
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(template.getName(), new WSChangeEvent<Template>(ChangeType.UPDATED, aTemplate));
		return aTemplate;
	}
	
	@Override
	@Transactional
	public AgentOption getAgentOption(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		EAgentOption options = this.agentOptionsDAO.findByTemplate(templateName);
		if (options == null) {
			options = this.templateHandler.createAgentOptions(templateName);
		}
		return options.toApi();
	}
	
	@Override
	@Transactional
	public AgentOption saveAgentOption(String templateName, AgentOption option) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotNull(option);
		EAgentOption options = this.agentOptionsDAO.findByTemplate(templateName);
		if (options == null) {
			options = this.templateHandler.createAgentOptions(templateName);
		}
		options = this.templateHandler.updateEntity(options, option);
		return options.toApi();
	}
	
	@Override
	@Transactional
	public SSHKey[] getSSHKeysForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		Set<SSHKey> keys = this.sshKeyHandler.getSSHKeyForTemplate(templateName);
		return keys.toArray(new SSHKey[keys.size()]);
	}
}
