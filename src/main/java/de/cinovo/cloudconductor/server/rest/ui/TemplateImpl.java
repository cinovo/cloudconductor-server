package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.*;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.handler.ServiceDefaultStateHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.*;
import de.cinovo.cloudconductor.server.util.comparators.TemplatePackageDiffer;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.template.TemplateDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.template.TemplatesWSHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;
import java.util.*;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class TemplateImpl implements ITemplate {

	@Autowired
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IServiceDefaultStateDAO serviceDefaultStateDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IPackageVersionDAO pkgVersionDAO;

	@Autowired
	private HostHandler hostHandler;
	@Autowired
	private ServiceDefaultStateHandler serviceDefaultStateHandler;
	@Autowired
	private SSHHandler sshKeyHandler;
	@Autowired
	private TemplateHandler templateHandler;
	@Autowired
	private TemplatePackageDiffer templatePackageComparator;
	
	@Autowired
	private TemplatesWSHandler templatesWSHandler;
	@Autowired
	private TemplateDetailWSHandler templateDetailWSHandler;
	
	@Override
	@Transactional
	public Template[] get() {
		return this.templateDAO.findList().stream().map(ETemplate::toApi).toArray(Template[]::new);
	}

	@Override
	@Transactional
	public SimpleTemplate[] getSimpleTemplates() {
		List<SimpleTemplate> list = new ArrayList<>();
		for (SimpleTemplate simpleTemplate : this.templateDAO.findSimpleList()) {
			simpleTemplate.getRepos().addAll(this.repoDAO.findNamesByTemplate(simpleTemplate.getName()));
			list.add(simpleTemplate);
		}
		return list.toArray(new SimpleTemplate[0]);
	}

	@Override
	@Transactional
	public void save(Template template) {
		RESTAssert.assertNotNull(template);
		RESTAssert.assertNotEmpty(template.getName());
		ETemplate eTemplate = this.templateDAO.findByName(template.getName());
		if(eTemplate == null) {
			eTemplate = this.templateHandler.createEntity(template);
			this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.ADDED, eTemplate.toApi()));
		} else {
			eTemplate = this.templateHandler.updateEntity(eTemplate, template);
			Template aTemplate = eTemplate.toApi();
			this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
			this.templateDetailWSHandler.broadcastChange(eTemplate.getName(), new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
			this.hostHandler.updateHostDetails(eTemplate);
		}
	}

	@Override
	@Transactional
	public void delete(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate eTemplate = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(eTemplate, Status.NOT_FOUND);
		this.templateDAO.delete(eTemplate);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.DELETED, eTemplate.toApi()));
	}
	
	private ETemplate getTemplateByName(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template, Status.NOT_FOUND);
		return template;
	}

	@Override
	@Transactional
	public Template get(String templateName) {
		return this.getTemplateByName(templateName).toApi();
	}

	@Override
	@Transactional
	public Template updatePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.updatePackage(template, packageName);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi();
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}

	@Override
	@Transactional
	public Template deletePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.removePackage(template, packageName);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi();
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(template.getName(), new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}

	@Override
	@Transactional
	public AgentOption getAgentOption(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		AgentOption options = this.agentOptionsDAO.findFlatByTemplate(templateName);
		if (options != null) {
			return options;
		}
		return this.templateHandler.createAgentOptions(templateName).toApi();
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

	@Override
	@Transactional
	public SSHKey[] getSSHKeysForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertTrue(this.templateDAO.exists(templateName), Status.NOT_FOUND);
		Set<SSHKey> keys = this.sshKeyHandler.getSSHKeyForTemplate(templateName);
		return keys.toArray(new SSHKey[0]);
	}

	@Override
	@Transactional
	public Service[] getServicesForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertTrue(this.templateDAO.exists(templateName), Status.NOT_FOUND);
		return this.serviceDAO.findByTemplate(templateName).stream().map(EService::toApi).toArray(Service[]::new);
	}

	@Override
	@Transactional
	public Repo[] getReposForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertTrue(this.templateDAO.exists(templateName), Status.NOT_FOUND);
		return repoDAO.findByTemplate(templateName).stream().map(ERepo::toApi).toArray(Repo[]::new);
	}

	@Override
	@Transactional
	public PackageVersion[] getPackageVersionsForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertTrue(this.templateDAO.exists(templateName), Status.NOT_FOUND);
		return pkgVersionDAO.findByTemplate(templateName).stream().map(EPackageVersion::toApi).distinct().toArray(PackageVersion[]::new);
	}
	
	@Override
	@Transactional
	public Template replacePackageVersionsForTemplate(String templateName, List<SimplePackageVersion> packageVersions) {
		ETemplate template = this.templateHandler.replacePackageVersionsForTemplate(packageVersions, templateName);
		Template aTemplate = template.toApi();
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}
	
	@Override
	@Transactional
	public SimplePackageVersion[] getSimplePackageVersionsForTemplate(String templateName) {
		return this.getTemplateByName(templateName).getPackageVersions().stream() //
				.map(EPackageVersion::toSimpleApi) //
				.sorted(Comparator.comparing(SimplePackageVersion::getName)) //
				.toArray(SimplePackageVersion[]::new);
	}
	
	@Override
	@Transactional
	public PackageDiff[] packageDiff(String templateA, String templateB) {
		return this.templatePackageComparator.compare(templateA, templateB);
	}

	@Override
	@Transactional
	public ServiceDefaultState[] getServiceDefaultStates(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertTrue(this.templateDAO.exists(templateName), Status.NOT_FOUND);
		return this.serviceDefaultStateDAO.findFlatByTemplate(templateName).toArray(new ServiceDefaultState[0]);
	}

	@Override
	@Transactional
	public ServiceDefaultState getServiceDefaultState(String templateName, String serviceName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(serviceName);
		return this.serviceDefaultStateDAO.findFlatByName(serviceName, templateName);
	}

	@Override
	@Transactional
	public ServiceDefaultState saveServiceDefaultState(String templateName, String serviceName, ServiceDefaultState newServiceDefaultState) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(serviceName);
		RESTAssert.assertNotNull(newServiceDefaultState);
		ServiceState newDefaultState = newServiceDefaultState.getState();
		RESTAssert.assertTrue(newDefaultState.equals(ServiceState.STOPPED) || newDefaultState.equals(ServiceState.STARTED));

		EServiceDefaultState esds = this.serviceDefaultStateHandler.updateServiceDefaultState(templateName, serviceName, newDefaultState);

		RESTAssert.assertNotNull(esds);
		return esds.toApi();
	}

}
