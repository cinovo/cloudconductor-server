package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageHandler {

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IDependencyDAO dependencyDAO;
	@Autowired
	private ITemplateDAO templateDAO;

	/**
	 * @param pv the package version
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackage createPackageFromVersion(PackageVersion pv) throws WebApplicationException {
		EPackage epackage = new EPackage();
		epackage.setName(pv.getName());
		epackage.setDescription("Auto-generated from repository updateEntity on " + sdf.format(Calendar.getInstance().getTime()) + ".");
		return this.packageDAO.save(epackage);
	}

	/**
	 * @param pv the package version
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion createEntity(PackageVersion pv) throws WebApplicationException {
		EPackageVersion et = new EPackageVersion();
		et = this.fillFields(et, pv);
		RESTAssert.assertNotNull(et);
		return this.packageVersionDAO.save(et);
	}

	/**
	 * @param et the entity to update
	 * @param pv the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion updateEntity(EPackageVersion et, PackageVersion pv) throws WebApplicationException {
		et = this.fillFields(et, pv);
		RESTAssert.assertNotNull(et);
		return this.packageVersionDAO.save(et);
	}

	/**
	 * @param dep the dependency
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EDependency createEntity(Dependency dep) throws WebApplicationException {
		EDependency edep = new EDependency();
		edep = this.fillFields(edep, dep);
		RESTAssert.assertNotNull(edep);
		return this.dependencyDAO.save(edep);
	}

	/**
	 * @param edep the entity to update
	 * @param dep  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EDependency updateEntity(EDependency edep, Dependency dep) throws WebApplicationException {
		edep = this.fillFields(edep, dep);
		RESTAssert.assertNotNull(edep);
		return this.dependencyDAO.save(edep);
	}

	/**
	 * @param repos collection of repos names
	 * @return a set of the repo entities
	 */
	public Set<ERepo> getRepos(Collection<String> repos) {
		HashSet<ERepo> result = new HashSet<>();
		for(String repo : repos) {
			result.add(this.repoDAO.findByName(repo));
		}
		return result;
	}

	/**
	 * @param epackage the package
	 * @param repos    the repos to look in
	 * @return the newest verison of the package in the provided repos
	 */
	public EPackageVersion getNewestPackageInRepo(EPackage epackage, Collection<ERepo> repos) {
		if(repos == null || repos.isEmpty() || epackage == null || epackage.getVersions().isEmpty()) {
			return null;
		}
		PackageVersionComparator versionComp = new PackageVersionComparator();

		List<EPackageVersion> result = new ArrayList<>();
		for(EPackageVersion ePackageVersion : epackage.getVersions()) {
			for(ERepo repo : ePackageVersion.getRepos()) {
				if(repos.contains(repo)) {
					result.add(ePackageVersion);
					break;
				}
			}
		}
		if(result.isEmpty()) {
			return null;
		}

		Collections.sort(result, versionComp);
		return result.get(result.size() - 1);
	}

	/**
	 * @param version the version
	 * @return true, if any template ueses the version
	 */
	public boolean checkIfInUse(EPackageVersion version) {
		for(ETemplate t : this.templateDAO.findList()) {
			if(t.getPackageVersions().contains(version)) {
				return true;
			}
		}
		return false;
	}

	private EPackageVersion fillFields(EPackageVersion epv, PackageVersion pv) {
		epv.setVersion(pv.getVersion());
		epv.setName(pv.getName());
		epv.setRepos(this.getRepos(pv.getRepos()));
		epv.setDeprecated(false);
		epv.setDependencies(new HashSet<EDependency>());
		for(Dependency dep : pv.getDependencies()) {
			EDependency eDependency = this.dependencyDAO.find(dep);
			if(eDependency == null) {
				eDependency = this.createEntity(dep);
			}
			epv.getDependencies().add(eDependency);
		}
		return epv;
	}


	private EDependency fillFields(EDependency edep, Dependency dep) {
		edep.setName(dep.getName());
		edep.setOperator(dep.getOperator());
		edep.setType(dep.getType());
		edep.setVersion(dep.getVersion());
		return edep;
	}

	/**
	 * @param version the version you want to check
	 * @param repos the repos you want to check
	 * @return true, if the version is contained in one of the given repos
	 */
	public boolean versionAvailableInRepo(EPackageVersion version, List<ERepo> repos) {
		for(ERepo repo : repos) {
			if(version.getRepos().contains(repo)) {
				return true;
			}
		}
		return false;
	}
}
