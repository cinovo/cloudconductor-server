package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
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
	private PackageHandler packageHandler;
	
	
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
	 * @param t the update data
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
	 * @param ao the update data
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
	public void updateAllPackages() {
		for (ETemplate t : this.templateDAO.findList()) {
			this.updateAllPackages(t);
		}
	}
	
	/**
	 * Updates all packages of a given template to the newest version
	 *
	 * @param template the template to update the packages for
	 */
	private void updateAllPackages(ETemplate template) {
		if ((template.getAutoUpdate() == null) || !template.getAutoUpdate()) {
			return;
		}
		
		List<EPackageVersion> list = new ArrayList<>(template.getPackageVersions());
		
		for (EPackageVersion version : template.getPackageVersions()) {
			EPackageVersion newest = this.packageHandler.getNewestPackageInRepos(version.getPkg(), template.getRepos());
			if (newest == null) {
				continue;
			}
			if (!newest.equals(version)) {
				list.remove(version);
				list.add(newest);
			}
		}
		
		template.setPackageVersions(list);
		this.templateDAO.save(template);
	}
	
	/**
	 * disables autoupdate for all templates
	 */
	public void disableAutoUpdate() {
		this.disableAutoUpdate(this.templateDAO.findList());
	}
	
	/**
	 * @param t collection of templates to disable autoupdate.
	 */
	private void disableAutoUpdate(Collection<ETemplate> t) {
		if (t == null) {
			t = this.templateDAO.findList();
		}
		for (ETemplate template : t) {
			if (template.getAutoUpdate()) {
				template.setAutoUpdate(false);
				this.templateDAO.save(template);
			}
		}
	}
	
	/**
	 * @param template the template to update the package in
	 * @param packageName the package to update
	 * @return the updated template
	 */
	public ETemplate updatePackage(ETemplate template, String packageName) {
		Map<EPackageVersion, EPackageVersion> removeAddMap = new HashMap<>();
		for (EPackageVersion version : template.getPackageVersions()) {
			if (version.getPkg().getName().equals(packageName)) {
				EPackageVersion newest = this.packageHandler.getNewestPackageInRepos(version.getPkg(), template.getRepos());
				if (newest == null) {
					continue;
				}
				if (!newest.equals(version)) {
					removeAddMap.put(version, newest);
				}
			}
		}
		for (Entry<EPackageVersion, EPackageVersion> entry : removeAddMap.entrySet()) {
			template.getPackageVersions().remove(entry.getKey());
			template.getPackageVersions().add(entry.getValue());
		}
		return template;
	}

	
	/**
	 * @param template the template to temove the package from
	 * @param packageName the package to update
	 * @return the updated template
	 */
	public ETemplate removePackage(ETemplate template, String packageName) {
		EPackageVersion remove = null;
		for (EPackageVersion version : template.getPackageVersions()) {
			if (version.getPkg().getName().equals(packageName)) {
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
		et.setAutoUpdate(t.getAutoUpdate() == null ? false : t.getAutoUpdate());
		et.setSmoothUpdate(t.getSmoothUpdate() == null ? false : t.getSmoothUpdate());

		if(t.getRepos() != null) {
			for(ERepo repo : this.repoDAO.findList()) {
				if(t.getRepos().contains(repo.getName())) {
					et.getRepos().add(repo);
				}
			}
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
		if (currentVersions != null) {
			for (EPackageVersion version : currentVersions) {
				if (targetVersions.containsKey(version.getName())) {
					if (version.getVersion().equals(targetVersions.get(version.getName()))) {
						if (this.packageHandler.versionAvailableInRepo(version, repos)) {
							result.add(version);
						}
					} else {
						EPackageVersion ePackageVersion = this.packageVersionDAO.find(version.getName(), targetVersions.get(version.getName()));
						if (ePackageVersion != null) {
							if (this.packageHandler.versionAvailableInRepo(ePackageVersion, repos)) {
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
	
}
