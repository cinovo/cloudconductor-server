package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

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

	@Override
	@Transactional
	public Set<Template> get() {
		Set<Template> result = new HashSet<>();
		for(ETemplate template : this.templateDAO.findList()) {
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
		if(eTemplate == null) {
			this.templateHandler.createEntity(template);
		} else {
			this.templateHandler.updateEntity(eTemplate, template);
		}
	}


	@Override
	@Transactional
	public void delete(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate eTemplate = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(eTemplate);
		this.templateDAO.delete(eTemplate);
	}

	@Override
	@Transactional
	public Template get(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
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
		this.templateDAO.save(template);
		return template.toApi();
	}

	@Override
	@Transactional
	public Template deletePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		template = this.templateHandler.removePackage(template, packageName);
		this.templateDAO.save(template);
		return template.toApi();
	}

	@Override
	@Transactional
	public AgentOption getAgentOption(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		EAgentOption options = this.agentOptionsDAO.findByTemplate(templateName);
		if(options == null) {
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
		if(options == null) {
			options = this.templateHandler.createAgentOptions(templateName);
		}
		options = this.templateHandler.updateEntity(options, option);
		return options.toApi();
	}
}
