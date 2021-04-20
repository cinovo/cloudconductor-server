package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.UpdateRange;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageHandler {
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final Pattern versionSeparator = Pattern.compile("[.-]");

	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IDependencyDAO dependencyDAO;
	
	/**
	 * @param pv the package version
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackage createPackageFromVersion(PackageVersion pv) throws WebApplicationException {
		EPackage epackage = new EPackage();
		epackage.setName(pv.getName());
		epackage.setDescription("Auto-generated from repository updateEntity on " + this.sdf.format(Calendar.getInstance().getTime()) + ".");
		return this.packageDAO.save(epackage);
	}
	
	/**
	 * @param pv  the package version
	 * @param pkg the package
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion createEntity(PackageVersion pv, EPackage pkg) throws WebApplicationException {
		EPackageVersion et = new EPackageVersion();
		this.fillFields(et, pv);
		RESTAssert.assertNotNull(et);
		et.setPkgId(pkg.getId());
		et.setPkgName(pkg.getName());
		return this.packageVersionDAO.save(et);
	}
	
	/**
	 * @param et the entity to update
	 * @param pv the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion updateEntity(EPackageVersion et, PackageVersion pv) throws WebApplicationException {
		this.fillFields(et, pv);
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
		this.fillFields(edep, dep);
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
		this.fillFields(edep, dep);
		RESTAssert.assertNotNull(edep);
		return this.dependencyDAO.save(edep);
	}
	
	/**
	 * @param pv      the package version
	 * @param newRepo the new repo name to add;
	 * @return the package version
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion updateEntity(EPackageVersion pv, ERepo newRepo) throws WebApplicationException {
		RESTAssert.assertNotNull(pv);
		if (pv.getRepos() == null) {
			pv.setRepos(new HashSet<>());
		}
		if (newRepo != null) {
			pv.getRepos().add(newRepo.getId());
			return this.packageVersionDAO.save(pv);
		}
		return pv;
	}

	/**
	 * @param currentPV	the package version which is currently installed
	 * @param template	template
	 * @return latest provided package version in update range of template or null if none was found
	 */
	public EPackageVersion getLatestPackageInRepos(EPackageVersion currentPV, ETemplate template) {
		return this.getLatestPackageInRepos(currentPV.getPkgName(), currentPV.getVersion(), template.getRepos(), template.getUpdateRange());
	}

	/**
	 * @param currentPV		the package version which is currently installed
	 * @param repoIds		the providing repo ids
	 * @param customRange	custom update range
	 * @return latest provided package version or null if none was found
	 */
	public EPackageVersion getLatestPackageInRepos(EPackageVersion currentPV, Collection<Long> repoIds, UpdateRange customRange) {
		return this.getLatestPackageInRepos(currentPV.getPkgName(), currentPV.getVersion(), repoIds, customRange);
	}

	/**
	 * @param pkgName			name of the package
	 * @param currentVersion	current version installed
	 * @param availableRepoIds	available repo ids
	 * @param range				update range
	 * @return latest provided package version in update range or null if none was found
	 */
	public EPackageVersion getLatestPackageInRepos(String pkgName, String currentVersion, Collection<Long> availableRepoIds, UpdateRange range) {
		if (availableRepoIds == null || availableRepoIds.isEmpty() || pkgName == null || currentVersion == null) {
			return null;
		}

		return this.getProvidedPackageVersions(pkgName, currentVersion, availableRepoIds, range).stream().max(new PackageVersionComparator()).orElse(null);
	}
	
	/**
	 * @param currentPV the package version entity
	 * @param template	the template entity
	 * @return list of package versions within template update range
	 */
	public List<EPackageVersion> getProvidedPackageVersions(EPackageVersion currentPV, ETemplate template) {
		return this.getProvidedPackageVersions(currentPV.getPkgName(), currentPV.getVersion(), template.getRepos(), template.getUpdateRange());
	}
	
	/**
	 * @param pkgName			the name of the package
	 * @param currentVersion	the version installed
	 * @param availableRepoIds	the ids of the repositories available
	 * @param range				the update range to check
	 * @return list of provided package versions in update range
	 */
	private List<EPackageVersion> getProvidedPackageVersions(String pkgName, String currentVersion, Collection<Long> availableRepoIds, UpdateRange range) {
		String[] versionParts = this.versionSeparator.split(currentVersion);
		switch (range) {
			case all:
				return this.packageVersionDAO.findProvidedByPackage(pkgName, availableRepoIds);
			case major:
				return this.packageVersionDAO.findProvidedInRange(pkgName, availableRepoIds, versionParts[0]);
			case minor:
				return this.packageVersionDAO.findProvidedInRange(pkgName, availableRepoIds, versionParts[0], versionParts[1]);
			case patch:
				return this.packageVersionDAO.findProvidedInRange(pkgName, availableRepoIds, versionParts[0], versionParts[1], versionParts[2]);
			default:
				return Collections.emptyList();
		}
	}
	
	private void fillFields(EPackageVersion epv, PackageVersion pv) {
		epv.setVersion(pv.getVersion());
		epv.getRepos().addAll(this.repoDAO.findByNames(pv.getRepos()).stream().map(ERepo::getId).collect(Collectors.toList()));
		epv.setDependencies(new HashSet<>());
		for (Dependency dep : pv.getDependencies()) {
			EDependency eDependency = this.dependencyDAO.find(dep);
			if (eDependency == null) {
				eDependency = this.createEntity(dep);
			}
			epv.getDependencies().add(eDependency.getId());
		}
	}
	
	private void fillFields(EDependency edep, Dependency dep) {
		edep.setName(dep.getName());
		edep.setOperator(dep.getOperator());
		edep.setType(dep.getType());
		edep.setVersion(dep.getVersion());
	}
	
	/**
	 * @param version the version you want to check
	 * @param repos   the repos ids you want to check
	 * @return true, if the version is contained in one of the given repos
	 */
	boolean versionAvailableInRepo(EPackageVersion version, List<Long> repos) {
		for (Long repoId : repos) {
			if (version.getRepos().contains(repoId)) {
				return true;
			}
		}
		return false;
	}
}
