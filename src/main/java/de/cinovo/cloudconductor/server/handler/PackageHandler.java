package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageHandler {

	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private IPackageServerGroupDAO packageServerGroupDAO;
	@Autowired
	private IDependencyDAO dependencyDAO;
	@Autowired
	private ITemplateDAO templateDAO;


	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * @param pv the package version
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackage createPackageFromVersion(PackageVersion pv) throws WebApplicationException {
		EPackage epackage = new EPackage();
		epackage.setName(pv.getName());
		epackage.setDescription("Auto-generated from repository updateEntity on " + sdf.format(Calendar.getInstance().getTime()) + ".");
		return this.packageDAO.save(epackage);
	}

	/**
	 * @param pv the package version
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion createEntity(PackageVersion pv) throws WebApplicationException {
		EPackageVersion et = new EPackageVersion();
		et = this.fillFields(et, pv);
		RESTAssert.assertNotNull(et);
		return this.packageVersionDAO.save(et);
	}

	/**
	 * @param et the entity to update
	 * @param pv the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EPackageVersion updateEntity(EPackageVersion et, PackageVersion pv) throws WebApplicationException {
		et = this.fillFields(et, pv);
		RESTAssert.assertNotNull(et);
		return this.packageVersionDAO.save(et);
	}

	/**
	 * @param dep the dependency
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EDependency createEntity(Dependency dep) throws WebApplicationException {
		EDependency edep = new EDependency();
		edep = this.fillFields(edep, dep);
		RESTAssert.assertNotNull(edep);
		return this.dependencyDAO.save(edep);
	}

	/**
	 * @param edep the entity to update
	 * @param dep  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EDependency updateEntity(EDependency edep, Dependency dep) throws WebApplicationException {
		edep = this.fillFields(edep, dep);
		RESTAssert.assertNotNull(edep);
		return this.dependencyDAO.save(edep);
	}

	/**
	 * @param psgs collection of package server group names
	 * @return a set of the package server group entities
	 */
	public Set<EPackageServerGroup> getPackageServerGroups(Collection<String> psgs) {
		HashSet<EPackageServerGroup> result = new HashSet<>();
		for(String psg : psgs) {
			result.add(this.packageServerGroupDAO.findByName(psg));
		}
		return result;
	}

	public boolean checkIfInUse(EPackageVersion version) {
		for(ETemplate t : this.templateDAO.findList()) {
			if(t.getPackageVersions().contains(version)) {
				return true;
			}
		}
		return false;
	}

	private EPackageVersion fillFields(EPackageVersion epv, PackageVersion pv) {
		epv.setVersion(pv.getVersion());
		epv.setName(pv.getName());
		epv.setServerGroups(this.getPackageServerGroups(pv.getPackageServerGroup()));
		epv.setDeprecated(false);
		epv.setDependencies(new HashSet<EDependency>());
		for(Dependency dep : pv.getDependencies()) {
			EDependency eDependency = this.dependencyDAO.find(dep);
			if(eDependency == null) {
				eDependency = this.createEntity(dep);
			}
			epv.getDependencies().add(eDependency);
		}
		return epv;
	}


	private EDependency fillFields(EDependency edep, Dependency dep) {
		edep.setName(dep.getName());
		edep.setOperator(dep.getOperator());
		edep.setType(dep.getType());
		edep.setVersion(dep.getVersion());
		return edep;
	}
}
