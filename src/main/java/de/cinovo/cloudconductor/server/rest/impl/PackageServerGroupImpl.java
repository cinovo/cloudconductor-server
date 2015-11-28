package de.cinovo.cloudconductor.server.rest.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IPackageServerGroup;
import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
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
	public void save(String name, PackageServerGroup apiObject) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public PackageServerGroup get(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void delete(String name) {
		// TODO Auto-generated method stub
		
	}
	
}
