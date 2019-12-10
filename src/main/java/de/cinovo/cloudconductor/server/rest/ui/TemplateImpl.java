package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.PackageDiff;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.ServiceDefaultState;
import de.cinovo.cloudconductor.api.model.SimplePackageVersion;
import de.cinovo.cloudconductor.api.model.SimpleTemplate;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.handler.ServiceDefaultStateHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.ETemplate;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IServiceDefaultStateDAO serviceDefaultStateDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	
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
		return this.templateDAO.findList().stream().map(ETemplate::toSimple).toArray(SimpleTemplate[]::new);
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

	@Override
	@Transactional
	public SSHKey[] getSSHKeysForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		Set<SSHKey> keys = this.sshKeyHandler.getSSHKeyForTemplate(templateName);
		return keys.toArray(new SSHKey[0]);
	}

	@Override
	@Transactional
	public Service[] getServicesForTemplate(String templateName) {
		ETemplate template = this.getTemplateByName(templateName);
		List<EPackageVersion> pvs = template.getPackageVersions();

		Set<Service> templateServices = new HashSet<>();
		for(EService service : this.serviceDAO.findList()) {
			for(EPackageVersion pv : pvs) {
				if(service.getPackages().contains(pv.getPkg())) {
					templateServices.add(service.toApi());
				}
			}
		}

		return templateServices.toArray(new Service[0]);
	}

	@Override
	@Transactional
	public Repo[] getReposForTemplate(String templateName) {
		ETemplate template = this.getTemplateByName(templateName);
		
		Set<Repo> repos = new HashSet<>();
		for(ERepo r : template.getRepos()) {
			repos.add(r.toApi());
		}

		return repos.toArray(new Repo[0]);
	}

	@Override
	@Transactional
	public PackageVersion[] getPackageVersionsForTemplate(String templateName) {
		ETemplate template = this.getTemplateByName(templateName);
		
		Set<PackageVersion> packageVersions = new HashSet<>();
		for(EPackageVersion packageVersion : template.getPackageVersions()) {
			packageVersions.add(packageVersion.toApi());
		}

		return packageVersions.toArray(new PackageVersion[0]);
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

		List<ServiceDefaultState> serviceDefaultStates = new ArrayList<>();
		for(EServiceDefaultState esds : this.serviceDefaultStateDAO.findByTemplate(templateName)) {
			serviceDefaultStates.add(esds.toApi());
		}

		return serviceDefaultStates.toArray(new ServiceDefaultState[0]);
	}

	@Override
	@Transactional
	public ServiceDefaultState getServiceDefaultState(String templateName, String serviceName) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(serviceName);

		EServiceDefaultState eServiceDefaultState = this.serviceDefaultStateDAO.findByName(serviceName, templateName);

		RESTAssert.assertNotNull(eServiceDefaultState, Status.NOT_FOUND);

		return eServiceDefaultState.toApi();
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
