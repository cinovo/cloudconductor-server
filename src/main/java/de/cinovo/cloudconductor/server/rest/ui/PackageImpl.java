package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.handler.PackageHandler;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

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
	private PackageHandler packageHandler;

	@Override
	@Transactional
	public Set<Package> get() {
		Set<Package> result = new HashSet<>();
		for(EPackage pkg  : this.packageDAO.findList()) {
			result.add(pkg.toApi());
		}
		return result;
	}

	@Override
	@Transactional
	public Package get(String pkgName) {
		return null;
	}

	@Override
	@Transactional
	public void save(Package pkg) {

	}

	@Override
	@Transactional
	public void delete(String pkgName) {

	}

	@Override
	@Transactional
	public Set<PackageVersion> getVersions(String pkgname) {
		return null;
	}

	@Override
	@Transactional
	public void addVersion(String pkgname, PackageVersion versionContent) {

	}

	@Override
	@Transactional
	public void removeVersion(String pkgname, String version) {

	}
}
