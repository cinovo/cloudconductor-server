package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackage;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.PackageStateChangeHandler;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.rest.utils.PaginationUtils;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
	private ITemplateDAO templateDAO;
	@Autowired
	private PackageStateChangeHandler packageStateChangeHandler;
	
	@Autowired
	private IDependencyDAO dependencyDAO;
	
	@Override
	@Transactional
	public Response get(int page, int pageSize, UriInfo uriInfo) {
		Set<Package> result = new HashSet<>();
		
		if (page == 0) {
			for (EPackage pkg : this.packageDAO.findList()) {
				result.add(pkg.toApi(this.packageVersionDAO));
			}
			return Response.ok(result.toArray(new Package[0])).header("x-total-count", result.size()).build();
		}
		
		int first = (page - 1) * pageSize;
		
		for (EPackage pkg : this.packageDAO.findList(first, pageSize)) {
			result.add(pkg.toApi(this.packageVersionDAO));
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
		return pkg.toApi(this.packageVersionDAO);
	}
	
	@Override
	@Transactional
	public PackageVersion[] getVersions(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		RESTAssert.assertTrue(this.packageDAO.exists(pkgName), Response.Status.NOT_FOUND);
		return this.packageVersionDAO.find(pkgName).stream().map(pv -> pv.toApi(this.repoDAO, this.dependencyDAO)).toArray(PackageVersion[]::new);
	}
	
	@Override
	@Transactional
	public Map<String, String> getUsage(String pkgName) {
		RESTAssert.assertNotEmpty(pkgName);
		EPackage pkg = this.packageDAO.findByName(pkgName);
		RESTAssert.assertNotNull(pkg);
		Map<String, String> res = new HashMap<>();
		List<ETemplate> templates = this.templateDAO.findList();
		for (ETemplate template : templates) {
			List<EPackageVersion> pvcs = this.packageVersionDAO.findByIds(template.getPackageVersions());
			Optional<EPackageVersion> first = pvcs.stream().filter(pv -> pv.getPkgId().equals(pkg.getId())).findFirst();
			first.ifPresent(ePackageVersion -> res.put(template.getName(), ePackageVersion.getVersion()));
		}
		return res;
	}
	
	@Override
	@Transactional
	public PackageVersion[] getVersionsForRepo(String repoName) {
		RESTAssert.assertNotEmpty(repoName);
		ERepo byName = this.repoDAO.findByName(repoName);
		RESTAssert.assertNotNull(byName);
		return this.packageVersionDAO.findByRepo(byName.getId()).stream().map(pv -> pv.toApi(this.repoDAO, this.dependencyDAO)).toArray(PackageVersion[]::new);
	}
	
	@Override
	@Transactional
	public PackageStateChanges getPackageChanges(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost host = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(host);
		if (host.getTemplateId() == null) {
			return new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}
		return this.packageStateChangeHandler.computePackageDiff(host);
	}
	
}
