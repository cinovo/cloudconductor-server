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
import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.JaxRsComponent;

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
	public PackageServerGroup get(String name) {
		RESTAssert.assertNotEmpty(name);
		EPackageServerGroup psg = this.dpsg.findByName(name);
		if(psg == null) {
			throw new NotFoundException();
		}
		return MAConverter.fromModel(psg);
	}

	@Override
	@Transactional
	public void newGroup(PackageServerGroup group) {
		RESTAssert.assertNotNull(group);
		RESTAssert.assertNotNull(group.getName());

		EPackageServerGroup g = new EPackageServerGroup();
		g.setName(group.getName());

		List<EPackageServer> packageServers = new ArrayList<>();
		for (Long id : group.getPackageServers()) {
			EPackageServer eps = this.dps.findById(id);
			if (eps != null) {
				packageServers.add(eps);
				if (eps.getId() == group.getPrimaryServer()) {
					g.setPrimaryServer(eps);
				}
			}
		}
		g.setPackageServers(packageServers);

		this.dpsg.save(g);
	}

	@Override
	@Transactional
	public void edit(Long id, PackageServerGroup group) {
		RESTAssert.assertNotNull(group);
		RESTAssert.assertNotNull(id);
		RESTAssert.assertNotNull(group.getName());

		EPackageServerGroup g = this.dpsg.findById(id);
		RESTAssert.assertNotNull(g);

		g.setName(group.getName());

		List<EPackageServer> packageServers = new ArrayList<>();
		for (Long psid : group.getPackageServers()) {
			EPackageServer eps = this.dps.findById(psid);
			if (eps != null) {
				packageServers.add(eps);
				if (eps.getId() == group.getPrimaryServer()) {
					g.setPrimaryServer(eps);
				}
			}
		}
		g.setPackageServers(packageServers);

		this.dpsg.save(g);
	}

	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EPackageServerGroup g = this.dpsg.findByName(name);
		RESTAssert.assertNotNull(g);
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
