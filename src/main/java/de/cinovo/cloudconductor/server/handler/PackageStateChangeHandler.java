package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.enums.PackageCommand;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class PackageStateChangeHandler {
	
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	@Autowired
	private IDependencyDAO dependencyDAO;
	
	/**
	 * @param host the host
	 * @return the package state changes
	 */
	public PackageStateChanges computePackageDiff(EHost host) {
		return this.computePackageDiff(host, host.getTemplateId());
	}
	
	/**
	 * @param host       the host
	 * @param templateId the template to compare with
	 * @return the package state changes
	 */
	private PackageStateChanges computePackageDiff(EHost host, Long templateId) {
		// Compute instruction lists (install/updateEntity/erase) from difference between packages actually installed packages that
		// should be installed.
		List<EPackageVersion> actual = this.packageVersionDAO.findByIds(this.packageStateDAO.findByHost(host.getId()).stream().map(EPackageState::getVersionId).collect(Collectors.toSet()));
		return this.computePackageDiff(templateId, actual);
	}
	
	/**
	 * @param templateId the referenced template
	 * @param actual     list of package versions which are actually installed
	 * @return multimap including package versions and the command which should be applied to them (e.g install, update, delete)
	 */
	private PackageStateChanges computePackageDiff(Long templateId, Collection<EPackageVersion> actual) {
		ETemplate template = this.templateDAO.findById(templateId);
		RESTAssert.assertNotNull(template);
		List<EPackageVersion> nominal = this.packageVersionDAO.findByIds(template.getPackageVersions());
		TreeSet<EPackageVersion> toInstall = this.findInstalls(actual, nominal);
		TreeSet<EPackageVersion> toErase;
		if (template.getNoUninstalls() == null || !template.getNoUninstalls()) {
			toErase = this.findDeletes(actual, nominal, this.repoDAO.findByIds(template.getRepos()));
		} else {
			toErase = new TreeSet<>(new PackageVersionComparator());
		}
		TreeSet<EPackageVersion> toUpdate = this.findUpdates(toInstall, toErase);
		
		// Convert the lists of package versions to lists of RPM descriptions (RPM name, release, and version).
		PackageStateChanges result = new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		this.fillPackageDiff(result, PackageCommand.INSTALL, toInstall);
		this.fillPackageDiff(result, PackageCommand.UPDATE, toUpdate);
		this.fillPackageDiff(result, PackageCommand.ERASE, toErase);
		return result;
	}
	
	private TreeSet<EPackageVersion> findInstalls(Collection<EPackageVersion> actual, List<EPackageVersion> nominal) {
		// Determine which package versions need to be erased and which are to be installed.
		TreeSet<EPackageVersion> toInstall = new TreeSet<>(new PackageVersionComparator());
		toInstall.addAll(nominal);
		toInstall.removeAll(actual);
		return toInstall;
	}
	
	private TreeSet<EPackageVersion> findDeletes(Collection<EPackageVersion> actual, List<EPackageVersion> nominal, List<ERepo> repos) {
		TreeSet<EPackageVersion> toErase = new TreeSet<>(new PackageVersionComparator());
		toErase.addAll(actual);
		toErase.removeAll(nominal);
		
		// get rid of reserved packages on erase
		Set<EPackageVersion> keep = new HashSet<>();
		EServerOptions eServerOptions = this.serverOptionsDAO.get();
		for (String pkg : eServerOptions.getDisallowUninstall()) {
			for (EPackageVersion erase : toErase) {
				if (erase.getPkgName().trim().equalsIgnoreCase(pkg.trim())) {
					keep.add(erase);
					break;
				}
			}
		}
		//keep packages from an repository not provided in the template
		for (EPackageVersion erase : toErase) {
			if (erase.getRepos().stream().noneMatch(r -> repos.stream().anyMatch(ir -> ir.getId().equals(r)))) {
				keep.add(erase);
			}
		}
		toErase.removeAll(keep);
		
		return toErase;
	}
	
	private TreeSet<EPackageVersion> findUpdates(TreeSet<EPackageVersion> toInstall, TreeSet<EPackageVersion> toErase) {
		// Resolve the removal of an older version and the installation of a newer one to an updateEntity instruction.
		TreeSet<EPackageVersion> toUpdate = new TreeSet<>(new PackageVersionComparator());
		for (EPackageVersion i : toInstall) {
			EPackageVersion e = toErase.lower(i);
			if ((e != null) && e.getPkgName().equals(i.getPkgName())) {
				toErase.remove(e);
				toUpdate.add(i);
			}
		}
		toInstall.removeAll(toUpdate);
		return toUpdate;
	}
	
	private void fillPackageDiff(PackageStateChanges changes, PackageCommand command, Collection<EPackageVersion> packageVersions) {
		for (EPackageVersion pv : packageVersions) {
			PackageVersion apiPV = pv.toApi(this.repoDAO, this.dependencyDAO);
			switch (command) {
				case INSTALL:
					changes.getToInstall().add(apiPV);
					break;
				case UPDATE:
					changes.getToUpdate().add(apiPV);
					break;
				case ERASE:
					changes.getToErase().add(apiPV);
					break;
			}
		}
	}
	
}
