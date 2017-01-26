package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class PackageImpl implements IPackage {

	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private ITemplateDAO templateDAO;


	@Override
	@Transactional
	public Set<Package> get() {
		Set<Package> result = new HashSet<>();
		for(EPackage pkg : this.packageDAO.findList()) {
			result.add(pkg.toApi());
		}
		return result;
	}

	@Override
	@Transactional
	public Package get(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		EPackage pkg = this.packageDAO.findByName(pkgName);
		RESTAssert.assertNotNull(pkg);
		return pkg.toApi();
	}

	@Override
	@Transactional
	public Set<PackageVersion> getVersions(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		Set<PackageVersion> result = new HashSet<>();
		List<EPackageVersion> versions = this.packageVersionDAO.find(pkgName);
		for(EPackageVersion version : versions) {
			result.add(version.toApi());
		}
		return result;
	}

	@Override
	@Transactional
	public Map<String, String> getUsage(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		EPackage pkg = this.packageDAO.findByName(pkgName);
		RESTAssert.assertNotNull(pkg);

		Map<String, String> result = new HashMap<>();
		List<ETemplate> templates = this.templateDAO.findByPackage(pkg);
		for(ETemplate template : templates) {
			for(EPackageVersion version : template.getPackageVersions()) {
				if(version.getPkg().getId().equals(pkg.getId())) {
					result.put(template.getName(), version.getVersion());
					break;
				}
			}
		}
		return result;
	}

	@Override
	@Transactional
	public Set<PackageVersion> getVersionsForRepo(String repoName) {
		RESTAssert.assertNotEmpty(repoName);
		List<EPackageVersion> versions = this.packageVersionDAO.findList();
		Set<PackageVersion> result = new HashSet<>();
		for(EPackageVersion version : versions) {
			for(ERepo repo : version.getRepos()) {
				if(repo.getName().equals(repoName)){
					result.add(version.toApi());
					break;
				}
			}
		}
		return result;
	}

}
