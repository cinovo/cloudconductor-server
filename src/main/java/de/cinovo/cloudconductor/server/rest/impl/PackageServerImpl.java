package de.cinovo.cloudconductor.server.rest.impl;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IPackageServer;
import de.cinovo.cloudconductor.api.model.PackageServer;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.taimos.springcxfdaemon.JaxRsComponent;

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
	public PackageServer getSimple(Long id) {
		EPackageServer eps = this.dps.findById(id);
		if (eps == null) {
			throw new NotFoundException();
		}
		PackageServer ps = new PackageServer(id, eps.getServerGroup().getName(), eps.getPath(), eps.getDescription(), eps.getIndexerType(), eps.getProviderType());
		return ps;
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		this.dps.deleteById(id);
	}
	
}
