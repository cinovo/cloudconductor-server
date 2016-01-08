package de.cinovo.cloudconductor.server.rest.impl;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IPackageServer;
import de.cinovo.cloudconductor.api.model.PackageServer;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
@JaxRsComponent
public class PackageServerImpl extends ImplHelper implements IPackageServer {
	
	@Autowired
	private IPackageServerDAO dps;
	@Autowired
	private IPackageServerGroupDAO dpsg;
	 
	
	@Override
	@Transactional
	public PackageServer get(Long id) {
		EPackageServer eps = this.dps.findById(id);
		if (eps == null) {
			throw new NotFoundException();
		}
		return MAConverter.fromModel(eps);
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		this.dps.deleteById(id);
	}

	@Override
	@Transactional
	public Long newServer(PackageServer ps) {
		RESTAssert.assertNotNull(ps);
		RESTAssert.assertNotNull(ps.getServerGroup());
		EPackageServerGroup epsg = this.dpsg.findById(ps.getServerGroup());
		RESTAssert.assertNotNull(epsg);

		EPackageServer eps = new EPackageServer();
		eps.setServerGroup(this.dpsg.findById(ps.getServerGroup()));
		eps.setAccessKeyId(ps.getAccessKeyId());
		eps.setAwsRegion(ps.getAwsRegion());
		eps.setBasePath(ps.getBasePath());
		eps.setBucketName(ps.getBucketName());
		eps.setDescription(ps.getDescription());
		eps.setIndexerType(ps.getIndexerType());
		eps.setPath(ps.getPath());
		eps.setProviderType(ps.getProviderType());
		eps.setSecretKey(ps.getSecretKey());
		
		eps = this.dps.save(eps);
		return eps.getId();
	}

	@Override
	@Transactional
	public void editServer(PackageServer ps) {
		RESTAssert.assertNotNull(ps);
		RESTAssert.assertNotNull(ps.getServerGroup());
		EPackageServer eps = this.dps.findById(ps.getId());
		if (eps == null) {
			throw new NotFoundException();
		}
		
		EPackageServerGroup epsg = this.dpsg.findById(ps.getServerGroup());
		RESTAssert.assertNotNull(epsg);
		
		eps.setServerGroup(this.dpsg.findById(ps.getServerGroup()));
		eps.setAccessKeyId(ps.getAccessKeyId());
		eps.setAwsRegion(ps.getAwsRegion());
		eps.setBasePath(ps.getBasePath());
		eps.setBucketName(ps.getBucketName());
		eps.setDescription(ps.getDescription());
		eps.setIndexerType(ps.getIndexerType());
		eps.setPath(ps.getPath());
		eps.setProviderType(ps.getProviderType());
		eps.setSecretKey(ps.getSecretKey());
		
		this.dps.save(eps);
	}
	
}
