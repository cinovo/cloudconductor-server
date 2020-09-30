package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.handler.PackageStateChangeHandler;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.rest.utils.PaginationUtils;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
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
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private IRepoDAO repoDAO;

	@Autowired
	private PackageStateChangeHandler packageStateChangeHandler;

	@Override
	@Transactional
	public Response get(int page, int pageSize, UriInfo uriInfo) {
		Set<Package> result = new HashSet<>();

		if(page == 0) {
			for(EPackage pkg : this.packageDAO.findList()) {
				result.add(pkg.toApi());
			}
			return Response.ok(result.toArray(new Package[0])).header("x-total-count", result.size()).build();
		}

		int first = (page - 1) * pageSize;

		for(EPackage pkg : this.packageDAO.findList(first, pageSize)) {
			result.add(pkg.toApi());
		}

		Long totalCount = this.packageDAO.count();
		String linkHeader = PaginationUtils.buildLinkHeader(uriInfo.getAbsolutePath().toString(), page, pageSize, totalCount);

		return Response.ok(result.toArray(new Package[0])).header("x-total-count", totalCount) //
				.header("Link", linkHeader) //
				.build();
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
	public PackageVersion[] getVersions(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		RESTAssert.assertTrue(this.packageDAO.exists(pkgName), Response.Status.NOT_FOUND);
		return this.packageVersionDAO.find(pkgName).stream().map(EPackageVersion::toApi).toArray(PackageVersion[]::new);
	}

	@Override
	@Transactional
	public Map<String, String> getUsage(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		RESTAssert.assertTrue(this.packageDAO.exists(pkgName), Response.Status.NOT_FOUND);
		return this.packageDAO.findPackageUsage(pkgName);
	}

	@Override
	@Transactional
	public PackageVersion[] getVersionsForRepo(String repoName) {
		RESTAssert.assertNotEmpty(repoName);
		RESTAssert.assertTrue(this.repoDAO.exists(repoName), Response.Status.NOT_FOUND);
		return this.packageVersionDAO.findByRepo(repoName).stream().map(EPackageVersion::toApi).toArray(PackageVersion[]::new);
	}

	@Override
	@Transactional
	public PackageStateChanges getPackageChanges(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost host = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(host);
		if(host.getTemplate() == null) {
			return new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
		return this.packageStateChangeHandler.computePackageDiff(host);
	}

}
