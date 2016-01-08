package de.cinovo.cloudconductor.server.rest.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IPackageServerGroup;
import de.cinovo.cloudconductor.api.model.PackageServer;
import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.model.ETemplate;
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
public class PackageServerGroupImpl extends ImplHelper implements IPackageServerGroup {

	@Autowired
	private IPackageServerDAO dps;
	@Autowired
	private IPackageServerGroupDAO dpsg;
	@Autowired
	private ITemplateDAO dtemplate;

	@Override
	@Transactional
	public PackageServerGroup[] get() {
		List<EPackageServerGroup> findList = this.dpsg.findList();
		Set<PackageServerGroup> result = new HashSet<>();
		for (EPackageServerGroup psg : findList) {
			result.add(MAConverter.fromModel(psg));
		}
		return result.toArray(new PackageServerGroup[result.size()]);
	}
	
	@Override
	@Transactional
	public PackageServerGroup get(Long id) {
		RESTAssert.assertNotNull(id);
		EPackageServerGroup psg = this.dpsg.findById(id);
		if(psg == null) {
			throw new NotFoundException();
		}
		return MAConverter.fromModel(psg);
	}

	@Override
	@Transactional
	public Long newGroup(PackageServerGroup group) {
		RESTAssert.assertNotNull(group);
		RESTAssert.assertNotNull(group.getName());

		EPackageServerGroup g = new EPackageServerGroup();
		g.setName(group.getName());

		List<EPackageServer> packageServers = new ArrayList<>();
		for (PackageServer ps : group.getPackageServers()) {
			EPackageServer eps = this.dps.findById(ps.getId());
			if (eps != null) {
				packageServers.add(eps);
				if (eps.getId() == group.getPrimaryServer()) {
					g.setPrimaryServerId(eps.getId());
				}
			}
		}
		g.setPackageServers(packageServers);
		g = this.dpsg.save(g);
		return g.getId();
	}

	@Override
	@Transactional
	public void edit(PackageServerGroup group) {
		RESTAssert.assertNotNull(group);
		RESTAssert.assertNotNull(group.getId());

		EPackageServerGroup g = this.dpsg.findById(group.getId());
		if (g == null) {
			throw new NotFoundException();
		}

		g.setName(group.getName());

		List<EPackageServer> packageServers = new ArrayList<>();
		for (PackageServer ps : group.getPackageServers()) {
			EPackageServer eps = this.dps.findById(ps.getId());
			if (eps != null) {
				packageServers.add(eps);
				if (eps.getId() == group.getPrimaryServer()) {
					g.setPrimaryServerId(eps.getId());
				}
			}
		}
		g.setPackageServers(packageServers);

		this.dpsg.save(g);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		RESTAssert.assertNotNull(id);
		EPackageServerGroup g = this.dpsg.findById(id);
		if (g == null) {
			throw new NotFoundException();
		}
		if (g.getPackageServers() != null && !g.getPackageServers().isEmpty()) {
			for (EPackageServer eps : g.getPackageServers()) {
				List<ETemplate> templates = this.dtemplate.findByPackageServer(eps);
				if (!templates.isEmpty()) {
					throw new WebApplicationException(Status.CONFLICT);
				}
				this.dps.delete(eps);
			}
		}
		this.dpsg.delete(g);
	}



}
