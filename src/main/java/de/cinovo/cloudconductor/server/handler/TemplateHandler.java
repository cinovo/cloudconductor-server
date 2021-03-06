package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.SimplePackageVersion;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
		this.fillFields(et, t);
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
		this.fillFields(et, t);
		RESTAssert.assertNotNull(et);
		return this.templateDAO.save(et);
	}
	
	/**
	 * @param template the template name
	 * @return the new agent options
	 */
	public EAgentOption createAgentOptions(ETemplate template) {
		EAgentOption eAgentOption = new EAgentOption();
		eAgentOption.setTemplateid(template.getId());
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
		List<ERepo> repos = this.repoDAO.findByIds(template.getRepos());
		Map<EPackageVersion, EPackageVersion> removeAddMap = new HashMap<>();
		for (EPackageVersion version : this.packageVersionDAO.findByIds(template.getPackageVersions())) {
			if (version.getPkgName().equals(packageName)) {
				EPackageVersion newest = this.packageHandler.getNewestPackageInRepos(version.getPkgId(), repos);
				if (newest == null) {
					continue;
				}
				if (!newest.equals(version)) {
					removeAddMap.put(version, newest);
				}
			}
		}
		for (Entry<EPackageVersion, EPackageVersion> entry : removeAddMap.entrySet()) {
			template.getPackageVersions().remove(entry.getKey().getId());
			template.getPackageVersions().add(entry.getValue().getId());
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
		for (EPackageVersion version : this.packageVersionDAO.findByIds(template.getPackageVersions())) {
			if (version.getPkgName().equals(packageName)) {
				remove = version;
				break;
			}
		}
		RESTAssert.assertNotNull(remove);
		if (remove != null) {
			template.getPackageVersions().remove(remove.getId());
		}
		return template;
	}
	
	private void fillFields(ETemplate et, Template t) {
		et.setName(t.getName());
		et.setDescription(t.getDescription());
		et.setRepos(new ArrayList<>());
		et.setAutoUpdate(t.getAutoUpdate() != null && t.getAutoUpdate());
		et.setSmoothUpdate(t.getSmoothUpdate() != null && t.getSmoothUpdate());
		et.setGroup(t.getGroup());
		if (t.getRepos() != null && !t.getRepos().isEmpty()) {
			et.getRepos().addAll(this.repoDAO.findByNames(t.getRepos()).stream().map(ERepo::getId).collect(Collectors.toList()));
		}
		et.setPackageVersions(this.findPackageVersions(et.getPackageVersions(), t.getVersions(), et.getRepos()));
	}
	
	private EAgentOption fillFields(EAgentOption eao, AgentOption ao) {
		EAgentOption result = GenericModelApiConverter.convert(ao, EAgentOption.class);
		result.setId(eao.getId());
		result.setTemplateid(eao.getTemplateid());
		return result;
	}
	
	private List<Long> findPackageVersions(List<Long> currentVersions, Map<String, String> targetVersions, List<Long> repos) {
		List<Long> result = new ArrayList<>();
		if (currentVersions != null) {
			for (EPackageVersion version : this.packageVersionDAO.findByIds(currentVersions)) {
				if (targetVersions.containsKey(version.getName())) {
					if (version.getVersion().equals(targetVersions.get(version.getName()))) {
						if (this.packageHandler.versionAvailableInRepo(version, repos)) {
							result.add(version.getId());
						}
					} else {
						EPackageVersion ePackageVersion = this.packageVersionDAO.find(version.getName(), targetVersions.get(version.getName()));
						if (ePackageVersion != null) {
							if (this.packageHandler.versionAvailableInRepo(ePackageVersion, repos)) {
								result.add(ePackageVersion.getId());
							}
						}
					}
					targetVersions.remove(version.getName());
				}
			}
		}
		if (targetVersions != null) {
			for (Entry<String, String> target : targetVersions.entrySet()) {
				EPackageVersion ePackageVersion = this.packageVersionDAO.find(target.getKey(), target.getValue());
				if (ePackageVersion != null) {
					if (this.packageHandler.versionAvailableInRepo(ePackageVersion, repos)) {
						result.add(ePackageVersion.getId());
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * @param packageVersions complete list of all package versions which should be installed
	 * @param templateName    name of the template to be modified
	 * @return the modified template
	 */
	public ETemplate replacePackageVersionsForTemplate(List<SimplePackageVersion> packageVersions, String templateName) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template, Status.NOT_FOUND);
		
		List<Long> newPVs = new ArrayList<>();
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
			newPVs.add(packageVersion.getId());
		}
		
		// replace pvs and save
		template.getPackageVersions().clear();
		template.getPackageVersions().addAll(newPVs);
		return this.templateDAO.save(template);
	}
	
	private boolean isRepoAvailableForTemplate(Collection<String> providingRepos, ETemplate template) {
		List<Long> availableRepos = template.getRepos();
		if (availableRepos == null || availableRepos.isEmpty()) {
			return false;
		}
		List<ERepo> repos = this.repoDAO.findByIds(availableRepos);
		if (repos == null || repos.isEmpty()) {
			return false;
		}
		for (String repoName : providingRepos) {
			for (ERepo availableRepo : repos) {
				if (availableRepo.getName().equals(repoName)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
