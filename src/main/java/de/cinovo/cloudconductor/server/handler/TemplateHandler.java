package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.SimplePackageVersion;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.template.TemplateDetailWSHandler;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class TemplateHandler {

	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IHostDAO hostDAO;

	@Autowired
	private PackageHandler packageHandler;

	@Autowired
	private TemplateDetailWSHandler templateDetailWSHandler;
	@Autowired
	private HostDetailWSHandler hostDetailWSHandler;


	/**
	 * @param t the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public ETemplate createEntity(Template t) throws WebApplicationException {
		ETemplate et = new ETemplate();
		et = this.fillFields(et, t);
		RESTAssert.assertNotNull(et);
		return this.templateDAO.save(et);
	}

	/**
	 * @param et the entity to update
	 * @param t  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public ETemplate updateEntity(ETemplate et, Template t) throws WebApplicationException {
		et = this.fillFields(et, t);
		RESTAssert.assertNotNull(et);
		return this.templateDAO.save(et);
	}

	/**
	 * @param templateName the template name
	 * @return the new agent options
	 */
	public EAgentOption createAgentOptions(String templateName) {
		EAgentOption eAgentOption = new EAgentOption();
		eAgentOption.setTemplate(this.templateDAO.findByName(templateName));
		return this.agentOptionsDAO.save(eAgentOption);
	}

	/**
	 * @param eao the entity to update
	 * @param ao  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EAgentOption updateEntity(EAgentOption eao, AgentOption ao) throws WebApplicationException {
		eao = this.fillFields(eao, ao);
		RESTAssert.assertNotNull(eao);
		return this.agentOptionsDAO.save(eao);
	}

	/**
	 * updates all packages of all templates to the newest version
	 */
	@Transactional
	public void updateAllPackages() {
		for(ETemplate t : this.templateDAO.findList()) {
			ETemplate updatedTemplate = this.updateAllPackages(t);
			this.sendTemplateUpdate(updatedTemplate);
		}
	}

	private void sendTemplateUpdate(ETemplate template) {
		this.templateDetailWSHandler.broadcastChange(template.getName(), new WSChangeEvent<>(ChangeType.UPDATED, template.toApi()));
	}

	/**
	 * Updates all packages of a given template to the newest version
	 *
	 * @param template the template to update the packages for
	 */
	@Transactional
	public ETemplate updateAllPackages(ETemplate template) {
		if((template.getAutoUpdate() == null) || !template.getAutoUpdate()) {
			return template;
		}

		boolean updatedPackage = false;
		List<EPackageVersion> list = new ArrayList<>(template.getPackageVersions());

		for(EPackageVersion version : template.getPackageVersions()) {
			EPackageVersion newest = this.packageHandler.getNewestPackageInRepos(version.getPkg(), template.getRepos());
			if(newest == null) {
				continue;
			}
			if(!newest.equals(version)) {
				list.remove(version);
				list.add(newest);
				updatedPackage = true;
			}
		}

		if(updatedPackage) {
			template.setPackageVersions(list);
			ETemplate updatedTemplate = this.templateDAO.save(template);
			List<EHost> affectedHosts = this.hostDAO.findHostsForTemplate(updatedTemplate.getName());
			affectedHosts.stream().forEach(host -> {
				this.hostDetailWSHandler.broadcastChange(host.getUuid(), new WSChangeEvent<>(ChangeType.UPDATED, host.toApi()));
			});

			return updatedTemplate;
		}

		return template;
	}

	/**
	 * disables auto update for all templates
	 */
	public void disableAutoUpdate() {
		this.templateDAO.disableAutoUpdate();
	}

	/**
	 * @param template    the template to update the package in
	 * @param packageName the package to update
	 * @return the updated template
	 */
	public ETemplate updatePackage(ETemplate template, String packageName) {
		Map<EPackageVersion, EPackageVersion> removeAddMap = new HashMap<>();
		for(EPackageVersion version : template.getPackageVersions()) {
			if(version.getPkg().getName().equals(packageName)) {
				EPackageVersion newest = this.packageHandler.getNewestPackageInRepos(version.getPkg(), template.getRepos());
				if(newest == null) {
					continue;
				}
				if(!newest.equals(version)) {
					removeAddMap.put(version, newest);
				}
			}
		}
		for(Entry<EPackageVersion, EPackageVersion> entry : removeAddMap.entrySet()) {
			template.getPackageVersions().remove(entry.getKey());
			template.getPackageVersions().add(entry.getValue());
		}
		return template;
	}

	/**
	 * @param template    the template to temove the package from
	 * @param packageName the package to update
	 * @return the updated template
	 */
	public ETemplate removePackage(ETemplate template, String packageName) {
		EPackageVersion remove = null;
		for(EPackageVersion version : template.getPackageVersions()) {
			if(version.getPkg().getName().equals(packageName)) {
				remove = version;
				break;
			}
		}
		RESTAssert.assertNotNull(remove);
		template.getPackageVersions().remove(remove);
		return template;
	}

	private ETemplate fillFields(ETemplate et, Template t) {
		et.setName(t.getName());
		et.setDescription(t.getDescription());
		et.setRepos(new ArrayList<>());
		et.setAutoUpdate(t.getAutoUpdate() != null && t.getAutoUpdate());
		et.setSmoothUpdate(t.getSmoothUpdate() != null && t.getSmoothUpdate());
		et.setGroup(t.getGroup());
		if(t.getRepos() != null && !t.getRepos().isEmpty()) {
			et.getRepos().addAll(this.repoDAO.findByNames(t.getRepos()));
		}
		et.setPackageVersions(this.findPackageVersions(et.getPackageVersions(), t.getVersions(), et.getRepos()));
		return et;
	}

	private EAgentOption fillFields(EAgentOption eao, AgentOption ao) {
		EAgentOption result = GenericModelApiConverter.convert(ao, EAgentOption.class);
		result.setId(eao.getId());
		result.setTemplate(eao.getTemplate());
		return result;
	}

	private List<EPackageVersion> findPackageVersions(List<EPackageVersion> currentVersions, Map<String, String> targetVersions, List<ERepo> repos) {
		List<EPackageVersion> result = new ArrayList<>();
		if(currentVersions != null) {
			for(EPackageVersion version : currentVersions) {
				if(targetVersions.containsKey(version.getName())) {
					if(version.getVersion().equals(targetVersions.get(version.getName()))) {
						if(this.packageHandler.versionAvailableInRepo(version, repos)) {
							result.add(version);
						}
					} else {
						EPackageVersion ePackageVersion = this.packageVersionDAO.find(version.getName(), targetVersions.get(version.getName()));
						if(ePackageVersion != null) {
							if(this.packageHandler.versionAvailableInRepo(ePackageVersion, repos)) {
								result.add(ePackageVersion);
							}
						}
					}
					targetVersions.remove(version.getName());
				}
			}
		}
		if(targetVersions != null) {
			for(Entry<String, String> target : targetVersions.entrySet()) {
				EPackageVersion ePackageVersion = this.packageVersionDAO.find(target.getKey(), target.getValue());
				if(ePackageVersion != null) {
					if(this.packageHandler.versionAvailableInRepo(ePackageVersion, repos)) {
						result.add(ePackageVersion);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * @param packageVersions	complete list of all package versions which should be installed
	 * @param templateName		name of the template to be modified
	 * @return the modified template
	 */
	public ETemplate replacePackageVersionsForTemplate(List<SimplePackageVersion> packageVersions, String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template, Status.NOT_FOUND);
		
		List<EPackageVersion> newPVs = new ArrayList<>();
		for (SimplePackageVersion requestedPV : packageVersions) {
			if (!this.isRepoAvailableForTemplate(requestedPV.getRepos(), template)) {
				String errorMessage = "Package '" + requestedPV + "' can not be used in template '" + templateName + "' because none of the providing repos " + requestedPV.getRepos() + " is available";
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errorMessage).build());
			}
			
			EPackageVersion packageVersion = this.packageVersionDAO.find(requestedPV.getName(), requestedPV.getVersion());
			if (packageVersion == null) {
				String errorMessage = "Unknown Package '" + requestedPV + "'";
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errorMessage).build());
			}
			newPVs.add(packageVersion);
		}
		
		// replace pvs and save
		template.getPackageVersions().clear();
		template.getPackageVersions().addAll(newPVs);
		return this.templateDAO.save(template);
	}
	
	private boolean isRepoAvailableForTemplate(Collection<String> providingRepos, ETemplate template) {
		List<ERepo> availableRepos = template.getRepos();
		if (availableRepos == null || availableRepos.isEmpty()) {
			return false;
		}

		for (String repoName : providingRepos) {
			for (ERepo availableRepo : availableRepos) {
				if (availableRepo.getName().equals(repoName)) {
					return true;
				}
			}
		}
		return false;
	}

}
