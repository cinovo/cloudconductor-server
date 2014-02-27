package de.cinovo.cloudconductor.server.impl.rest;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IIOModule;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.tools.AMConverter;
import de.cinovo.cloudconductor.server.util.RPMComparator;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class IOModuleImpl extends ImplHelper implements IIOModule {
	
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private IFileDAO dcfg;
	@Autowired
	private ITemplateDAO dtemplate;
	@Autowired
	private IPackageVersionDAO drpm;
	@Autowired
	private IDependencyDAO ddep;
	@Autowired
	private AMConverter amc;
	
	
	@Override
	@Transactional
	public Response importVersions(Set<PackageVersion> rpms) {
		RESTAssert.assertNotEmpty(rpms);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, Set<String>> provided = new HashMap<>();
		
		for (PackageVersion rpm : rpms) {
			// Retrieve the package for the given RPM. Create it if it doesn't exist.
			EPackage mpkg = this.dpkg.findByName(rpm.getName());
			
			if (mpkg == null) { // there is no package for this RPM yet
				mpkg = new EPackage();
				mpkg.setName(rpm.getName());
				mpkg.setDescription("Auto-generated from repository update on " + sdf.format(Calendar.getInstance().getTime()) + ".");
				mpkg = this.dpkg.save(mpkg);
			}
			
			if (!provided.containsKey(mpkg.getName())) {
				provided.put(mpkg.getName(), new HashSet<String>());
			}
			
			// Check if we have this particular RPM version on record.
			EPackageVersion mrpm = this.drpm.find(rpm.getName(), rpm.getVersion());
			provided.get(mpkg.getName()).add(rpm.getVersion());
			
			// We already have this package version. Move on.
			if (mrpm != null) {
				mrpm.setDeprecated(false);
				this.drpm.save(mrpm);
				continue;
			}
			
			// Create a new version for the package.
			mrpm = this.amc.toModel(rpm);
			mrpm.setPkg(mpkg);
			
			// Convert API dependency object to entities, add them to the package version, and save.
			Set<EDependency> result = new HashSet<>();
			if (rpm.getDependencies() != null) {
				for (Dependency d : rpm.getDependencies()) {
					EDependency md = this.ddep.save(this.amc.toModel(d));
					result.add(md);
				}
			}
			mrpm.setDependencies(result);
			this.drpm.save(mrpm);
		}
		
		// perform db clean up
		List<EPackage> inDB = this.dpkg.findList();
		List<ETemplate> templates = this.dtemplate.findList();
		List<EFile> cfgs = this.dcfg.findList();
		for (EPackage pkg : inDB) {
			if (provided.containsKey(pkg.getName())) {
				// clean up rpm list
				this.handleRPMUsage(provided.get(pkg.getName()), pkg.getRPMs(), templates);
				continue;
			}
			// check if it's used somewhere within configfiles
			for (EFile cfg : cfgs) {
				if ((cfg.getPkg() != null) && cfg.getPkg().equals(pkg)) {
					cfg.setPkg(null);
					this.dcfg.save(cfg);
				}
			}
			// clean up rpm list
			boolean inUse = this.handleRPMUsage(null, pkg.getRPMs(), templates);
			if (!inUse) {
				this.dpkg.deleteById(pkg.getId());
			}
		}
		this.autoUpdate(templates);
		return Response.ok().build();
	}
	
	private boolean handleRPMUsage(Set<String> provided, Set<EPackageVersion> existing, List<ETemplate> templates) {
		boolean deprecated = false;
		if (existing == null) {
			return deprecated;
		}
		for (EPackageVersion dbrpm : existing) {
			if ((provided != null) && provided.contains(dbrpm.getName())) {
				continue;
			}
			boolean found = false;
			// check if it's used somewhere
			for (ETemplate t : templates) {
				if (t.getRPMs().contains(dbrpm)) {
					// mark deprecated
					dbrpm.setDeprecated(true);
					found = true;
					break;
				}
			}
			if (found) {
				this.drpm.save(dbrpm);
				deprecated = true;
			} else {
				// delete it
				this.drpm.deleteById(dbrpm.getId());
			}
		}
		return deprecated;
	}
	
	private void autoUpdate(List<ETemplate> templates) {
		RPMComparator rpmComp = new RPMComparator();
		for (ETemplate t : templates) {
			if ((t.getAutoUpdate() == null) || !t.getAutoUpdate()) {
				continue;
			}
			Set<EPackageVersion> list = new HashSet<>(t.getRPMs());
			for (EPackageVersion rpm : t.getRPMs()) {
				List<EPackageVersion> eprpms = new ArrayList<>(rpm.getPkg().getRPMs());
				Collections.sort(eprpms, rpmComp);
				EPackageVersion newest = eprpms.get(eprpms.size() - 1);
				if (!newest.equals(rpm)) {
					list.remove(rpm);
					list.add(newest);
				}
			}
			t.setRPMs(list);
			this.dtemplate.save(t);
		}
	}
	
}
