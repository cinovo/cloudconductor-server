package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.text.SimpleDateFormat;
import java.util.*;
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
	 * @param pkgId the package id
	 * @param repos the repos to look in
	 * @return the latest package version in the provided repos
	 */
	public EPackageVersion getNewestPackageInRepos(Long pkgId, Collection<ERepo> repos) {
		if ((repos == null) || repos.isEmpty() || (pkgId == null)) {
			return null;
		}
		
		List<EPackageVersion> packageVersions = this.packageVersionDAO.findByPackage(pkgId);
		if (packageVersions.isEmpty()) {
			return null;
		}
		
		PackageVersionComparator versionComp = new PackageVersionComparator();
		
		List<EPackageVersion> existingVersions = new ArrayList<>(packageVersions);
		existingVersions.sort((left, right) -> -versionComp.compare(left, right));
		
		
		EPackageVersion version = null;
		for (EPackageVersion existingVersion : existingVersions) {
			if (version != null) {
				//we already found the newest one
				break;
			}
			for (Long repoId : existingVersion.getRepos()) {
				if (repos.stream().anyMatch(r -> r.getId().equals(repoId))) {
					version = existingVersion;
					break;
				}
			}
		}
		return version;
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
	public boolean versionAvailableInRepo(EPackageVersion version, List<Long> repos) {
		for (Long repoId : repos) {
			if (version.getRepos().contains(repoId)) {
				return true;
			}
		}
		return false;
	}
}
