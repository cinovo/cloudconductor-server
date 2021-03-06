package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.*;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.handler.ServiceDefaultStateHandler;
import de.cinovo.cloudconductor.server.handler.ServiceHandler;
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
import java.util.stream.Collectors;

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
	private IHostDAO hostDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	
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
	
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IRepoMirrorDAO mirrorDao;
	@Autowired
	private IDependencyDAO dependencyDAO;
	@Autowired
	private ServiceHandler serviceHandler;
	
	@Override
	@Transactional
	public Template[] get() {
		return this.templateDAO.findList().stream().map(t -> t.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO)).toArray(Template[]::new);
	}
	
	@Override
	@Transactional
	public SimpleTemplate[] getSimpleTemplates() {
		List<SimpleTemplate> list = new ArrayList<>();
		for (ETemplate template : this.templateDAO.findList()) {
			SimpleTemplate s = new SimpleTemplate();
			s.setGroup(template.getGroup());
			s.setName(template.getName());
			s.setHostCount(Math.toIntExact(this.hostDAO.countForTemplate(template.getId())));
			s.setPackageCount(template.getPackageVersions().size());
			s.setRepos(this.repoDAO.findByIds(template.getRepos()).stream().map(ERepo::getName).collect(Collectors.toSet()));
			list.add(s);
		}
		return list.toArray(new SimpleTemplate[0]);
	}
	
	@Override
	@Transactional
	public void save(Template template) {
		RESTAssert.assertNotNull(template);
		RESTAssert.assertNotEmpty(template.getName());
		ETemplate eTemplate = this.templateDAO.findByName(template.getName());
		if (eTemplate == null) {
			eTemplate = this.templateHandler.createEntity(template);
			this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.ADDED, eTemplate.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO)));
		} else {
			eTemplate = this.templateHandler.updateEntity(eTemplate, template);
			Template aTemplate = eTemplate.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
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
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.DELETED, eTemplate.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO)));
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
		return this.getTemplateByName(templateName).toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
	}
	
	@Override
	@Transactional
	public Template updatePackage(String templateName, String packageName) {
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.updatePackage(template, packageName);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
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
		Template aTemplate = template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(template.getName(), new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}
	
	@Override
	@Transactional
	public AgentOption getAgentOption(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		EAgentOption options = this.agentOptionsDAO.findByTemplate(template);
		if (options != null) {
			return options.toApi(template);
		}
		return this.templateHandler.createAgentOptions(template).toApi(template);
	}
	
	@Override
	@Transactional
	public AgentOption saveAgentOption(String templateName, AgentOption option) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotNull(option);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		EAgentOption options = this.agentOptionsDAO.findByTemplate(template);
		if (options == null) {
			options = this.templateHandler.createAgentOptions(template);
		}
		options = this.templateHandler.updateEntity(options, option);
		return options.toApi(template);
	}
	
	@Override
	@Transactional
	public SSHKey[] getSSHKeysForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		Set<SSHKey> keys = this.sshKeyHandler.getSSHKeyForTemplate(template);
		return keys.toArray(new SSHKey[0]);
	}
	
	@Override
	@Transactional
	public Service[] getServicesForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		return this.serviceHandler.findByTemplate(template).stream().map(s -> s.toApi(this.packageDAO)).toArray(Service[]::new);
	}
	
	@Override
	@Transactional
	public Repo[] getReposForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		return this.repoDAO.findByIds(template.getRepos()).stream().map(r -> r.toApi(this.mirrorDao)).toArray(Repo[]::new);
	}
	
	@Override
	@Transactional
	public PackageVersion[] getPackageVersionsForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		return this.pkgVersionDAO.findByIds(template.getPackageVersions()).stream().map(pv -> pv.toApi(this.repoDAO, this.dependencyDAO)).distinct().toArray(PackageVersion[]::new);
	}
	
	@Override
	@Transactional
	public Template replacePackageVersionsForTemplate(String templateName, List<SimplePackageVersion> packageVersions) {
		ETemplate template = this.templateHandler.replacePackageVersionsForTemplate(packageVersions, templateName);
		Template aTemplate = template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}
	
	@Override
	@Transactional
	public SimplePackageVersion[] getSimplePackageVersionsForTemplate(String templateName) {
		return this.getTemplateByName(templateName).getPackageVersions().stream() //
				.map(pvId -> this.packageVersionDAO.findById(pvId).toSimpleApi(this.repoDAO)) //
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
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		return this.serviceDefaultStateDAO.findByTemplate(template.getId()).stream().map(s -> s.toApi(this.templateDAO, this.serviceDAO)).toArray(ServiceDefaultState[]::new);
	}
	
	@Override
	@Transactional
	public ServiceDefaultState getServiceDefaultState(String templateName, String serviceName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(serviceName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		EService service = this.serviceDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		return this.serviceDefaultStateDAO.findByServiceAndTemplate(service.getId(), template.getId()).toApi(this.templateDAO, this.serviceDAO);
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
		return esds.toApi(this.templateDAO, this.serviceDAO);
	}
	
}
