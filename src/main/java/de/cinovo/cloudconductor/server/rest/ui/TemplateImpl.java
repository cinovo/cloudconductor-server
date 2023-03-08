package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.enums.UpdateRange;
import de.cinovo.cloudconductor.api.interfaces.ITemplate;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.PackageDiff;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.PackageVersionUpdates;
import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.ServiceDefaultState;
import de.cinovo.cloudconductor.api.model.SimplePackageVersion;
import de.cinovo.cloudconductor.api.model.SimpleTemplate;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.handler.PackageHandler;
import de.cinovo.cloudconductor.server.handler.SSHHandler;
import de.cinovo.cloudconductor.server.handler.ServiceDefaultStateHandler;
import de.cinovo.cloudconductor.server.handler.ServiceHandler;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.*;
import de.cinovo.cloudconductor.server.util.comparators.TemplatePackageDiffer;
import de.cinovo.cloudconductor.server.util.comparators.VersionStringComparator;
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
	private PackageHandler pkgHandler;
	
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
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.updatePackage(template, packageName, template.getUpdateRange());
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}

	@Override
	@Transactional
	public Template updatePackage(String templateName, String packageName, UpdateRange range) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		RESTAssert.assertNotNull(range);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.updatePackage(template, packageName, range);
		template = this.templateDAO.save(template);
		Template aTemplate = template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO);
		this.templatesWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.templateDetailWSHandler.broadcastChange(templateName, new WSChangeEvent<>(ChangeType.UPDATED, aTemplate));
		this.hostHandler.updateHostDetails(template);
		return aTemplate;
	}

	@Override
	@Transactional
	public Template updatePackage(String templateName, String packageName, String targetVersion) {
		RESTAssert.assertNotEmpty(templateName);
		RESTAssert.assertNotEmpty(packageName);
		RESTAssert.assertNotNull(targetVersion);
		ETemplate template = this.getTemplateByName(templateName);
		template = this.templateHandler.updatePackage(template, packageName, targetVersion);
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
		ETemplate template = this.getTemplateByName(templateName);
		Set<Long> availableRepoIds = new HashSet<>(template.getRepos());
		
		List<SimplePackageVersion> simplePVs = new ArrayList<>();
		for (EPackageVersion pv : this.packageVersionDAO.findByIds(template.getPackageVersions())) {
			Set<Long> repoIds = new HashSet<>(pv.getRepos());
			repoIds.retainAll(availableRepoIds);
			List<String> repoNames = this.repoDAO.findNamesByIds(repoIds);
			simplePVs.add(new SimplePackageVersion(pv.getPkgName(), pv.getVersion(), repoNames));
		}
		simplePVs.sort(Comparator.comparing(SimplePackageVersion::getName));
		return simplePVs.toArray(new SimplePackageVersion[0]);
	}

	@Override
	@Transactional
	public PackageVersionUpdates getPackageVersionUpdatesForTemplate(String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);

		VersionStringComparator comparator = new VersionStringComparator();

		// collect available PVs to multi-map
		Map<String, SortedSet<String>> availablePVMultiMap = new LinkedHashMap<>();
		for (EPackageVersion availablePV : this.packageVersionDAO.findByRepo(template.getRepos())) {
			availablePVMultiMap.computeIfAbsent(availablePV.getPkgName(), k -> new TreeSet<>(comparator));
			availablePVMultiMap.get(availablePV.getPkgName()).add(availablePV.getVersion());
		}

		// collect PVs in range
		Map<String, SortedSet<String>> inRange = new LinkedHashMap<>();
		for (EPackageVersion installedPV : this.packageVersionDAO.findByIds(template.getPackageVersions())) {
			inRange.computeIfAbsent(installedPV.getPkgName(), k -> new TreeSet<>(comparator));
			Set<String> providedVersions = this.pkgHandler.getProvidedPackageVersions(installedPV, template).stream().map(EPackageVersion::getVersion).collect(Collectors.toSet());
			if (providedVersions.isEmpty()) {
				inRange.get(installedPV.getPkgName()).add(installedPV.getVersion());
				continue;
			}
			inRange.get(installedPV.getPkgName()).addAll(providedVersions);
		}
		return new PackageVersionUpdates(availablePVMultiMap, inRange);
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
