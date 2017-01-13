package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.PackageServer;
import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.IndexFileIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.RPMIndexer;
import de.cinovo.cloudconductor.server.repo.provider.AWSS3Provider;
import de.cinovo.cloudconductor.server.repo.provider.FileProvider;
import de.cinovo.cloudconductor.server.repo.provider.HTTPProvider;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageServerHandler {

	@Autowired
	private IPackageServerDAO packageServerDAO;
	@Autowired
	private IPackageServerGroupDAO packageServerGroupDAO;
	@Autowired
	private ITemplateDAO templateDAO;


	/**
	 * @param server the package server you want the repo provider for
	 * @return the repo provider
	 */
	public static IRepoProvider findRepoProvider(EPackageServer server) {
		if(server == null) {
			return null;
		}
		switch(server.getProviderType()) {
			case AWSS3:
				return new AWSS3Provider(server);
			case FILE:
				return new FileProvider(server);
			case HTTP:
				return new HTTPProvider(server);
			default:
				return null;
		}
	}

	/**
	 * @param server the package server you want the repo indexer for
	 * @return the repo indexer
	 */
	public static IRepoIndexer findRepoIndexer(EPackageServer server) {
		if(server == null) {
			return null;
		}
		switch(server.getIndexerType()) {
			case FILE:
				return new IndexFileIndexer();
			case RPM:
				return new RPMIndexer();
			default:
				return null;
		}
	}

	/**
	 * @param ps the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageServer createEntity(PackageServer ps) throws WebApplicationException {
		EPackageServer eps = new EPackageServer();
		eps = this.fillFields(eps, ps);
		RESTAssert.assertNotNull(eps);
		return this.packageServerDAO.save(eps);
	}

	/**
	 * @param eps the entity to update
	 * @param ps  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageServer updateEntity(EPackageServer eps, PackageServer ps) throws WebApplicationException {
		eps = this.fillFields(eps, ps);
		RESTAssert.assertNotNull(eps);
		return this.packageServerDAO.save(eps);
	}

	/**
	 * @param eps the package server entity
	 * @return whether the package server is in use by a template or not
	 */
	public boolean checkIfInUse(EPackageServer eps) {
		List<ETemplate> templates = this.templateDAO.findByPackageServer(eps);
		return !templates.isEmpty();
	}

	/**
	 * @param group the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageServerGroup createEntity(PackageServerGroup group) throws WebApplicationException {
		EPackageServerGroup g = new EPackageServerGroup();
		g = this.fillFields(g, group);
		RESTAssert.assertNotNull(g);
		return this.packageServerGroupDAO.save(g);
	}

	/**
	 * @param g     the entity to update
	 * @param group the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageServerGroup updateEntity(EPackageServerGroup g, PackageServerGroup group) throws WebApplicationException {
		g = this.fillFields(g, group);
		RESTAssert.assertNotNull(g);
		return this.packageServerGroupDAO.save(g);
	}

	/**
	 * Deletes a package server group and its sub package servers
	 *
	 * @param g the server group to delete
	 * @throws WebApplicationException on error
	 */
	public void deleteEntity(EPackageServerGroup g) throws WebApplicationException {
		if(g.getPackageServers() != null && !g.getPackageServers().isEmpty()) {
			for(EPackageServer eps : g.getPackageServers()) {
				if(this.checkIfInUse(eps)) {
					throw new WebApplicationException(Status.CONFLICT);
				}
				this.packageServerDAO.delete(eps);
			}
		}
		this.packageServerGroupDAO.delete(g);
	}

	/**
	 * @param group the package server group
	 * @return list of all package servers provided by the group
	 */
	public List<EPackageServer> getPackageServers(PackageServerGroup group) {
		List<EPackageServer> packageServers = new ArrayList<>();
		for(PackageServer ps : group.getPackageServers()) {
			EPackageServer eps = this.packageServerDAO.findById(ps.getId());
			if(eps != null) {
				packageServers.add(eps);
			}
		}
		return packageServers;
	}

	/**
	 * @param group the package server group
	 * @return the primary package server of that group
	 */
	public EPackageServer findPrimaryServer(EPackageServerGroup group) {
		for(EPackageServer ps : group.getPackageServers()) {
			if(Objects.equals(ps.getId(), group.getPrimaryServerId())) {
				return ps;
			}
		}
		return null;
	}

	private EPackageServerGroup fillFields(EPackageServerGroup g, PackageServerGroup group) {
		if(group.getName() != null && !group.getName().isEmpty()) {
			g.setName(group.getName());
		}
		List<EPackageServer> packageServers = this.getPackageServers(group);
		g.setPackageServers(packageServers);
		g.setPrimaryServerId(group.getPrimaryServer());
		if(group.getPackageServers() != null && this.findPrimaryServer(g) == null) {
			g.setPrimaryServerId(null);
		}
		return g;
	}

	private EPackageServer fillFields(EPackageServer eps, PackageServer ps) {
		EPackageServerGroup serverGroup = this.packageServerGroupDAO.findById(ps.getServerGroup());
		if(serverGroup == null) {
			return null;
		}
		eps.setServerGroup(serverGroup);
		eps.setAccessKeyId(ps.getAccessKeyId());
		eps.setAwsRegion(ps.getAwsRegion());
		eps.setBasePath(ps.getBasePath());
		eps.setBucketName(ps.getBucketName());
		eps.setDescription(ps.getDescription());
		eps.setIndexerType(ps.getIndexerType());
		eps.setPath(ps.getPath());
		eps.setProviderType(ps.getProviderType());
		eps.setSecretKey(ps.getSecretKey());
		return eps;
	}
}
