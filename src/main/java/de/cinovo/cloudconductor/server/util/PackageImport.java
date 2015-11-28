package de.cinovo.cloudconductor.server.util;

/*
 * #%L cloudconductor-server %% Copyright (C) 2013 - 2014 Cinovo AG %% Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.comparators.PackageVersionComparator;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Service
public class PackageImport implements IPackageImport {
	
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
	private IPackageServerGroupDAO dpsg;
	@Autowired
	private AMConverter amc;
	
	
	/**
	 * @param packageVersions
	 */
	@Override
	public void importVersions(Set<PackageVersion> packageVersions) {
		Map<String, Set<PackageVersion>> groupMap = new HashMap<>();
		for (PackageVersion packageVersion : packageVersions) {
			if (!groupMap.containsKey(packageVersion.getPackageServerGroup())) {
				groupMap.put(packageVersion.getPackageServerGroup(), new HashSet<PackageVersion>());
			}
			groupMap.get(packageVersion.getPackageServerGroup()).add(packageVersion);
		}
		
		for (Entry<String, Set<PackageVersion>> entry : groupMap.entrySet()) {
			this.importVersions(entry.getValue(), entry.getKey());
		}
	}
	
	@Override
	@Transactional
	public void importVersions(Set<PackageVersion> packageVersions, String packageServerGroupName) {
		RESTAssert.assertNotEmpty(packageVersions);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String, PackageVersion> provided = new HashMap<>();
		
		for (PackageVersion rpm : packageVersions) {
			// Retrieve the package for the given RPM. Create it if it doesn't exist.
			EPackage mpkg = this.dpkg.findByName(rpm.getName());
			
			if (mpkg == null) { // there is no package for this RPM yet
				mpkg = new EPackage();
				mpkg.setName(rpm.getName());
				mpkg.setDescription("Auto-generated from repository update on " + sdf.format(Calendar.getInstance().getTime()) + ".");
				mpkg = this.dpkg.save(mpkg);
			}
			
			// Check if we have this particular RPM version on record.
			EPackageVersion mrpm = this.drpm.find(rpm.getName(), rpm.getVersion());
			provided.put(mpkg.getName(), rpm);
			
			// We already have this package version. Move on.
			if (mrpm != null) {
				mrpm.setDeprecated(false);
				mrpm.getServerGroups().add(this.dpsg.findByName(rpm.getPackageServerGroup()));
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
			boolean inUse = this.handleRPMUsage(new PackageVersion(null, null, null, packageServerGroupName), pkg.getRPMs(), templates);
			if (!inUse) {
				this.dpkg.deleteById(pkg.getId());
			}
		}
		this.autoUpdate(templates);
	}
	
	private boolean handleRPMUsage(PackageVersion packageVersion, Set<EPackageVersion> existing, List<ETemplate> templates) {
		boolean deprecated = false;
		if (existing == null) {
			return deprecated;
		}
		for (EPackageVersion dbrpm : existing) {
			if ((packageVersion != null) && (packageVersion.getName() != null) && packageVersion.getName().equals(dbrpm.getName())) {
				continue;
			}
			boolean found = false;
			// check if other mirrors are still out there
			for (EPackageServerGroup svg : dbrpm.getServerGroups()) {
				if ((packageVersion != null) && svg.getName().equals(packageVersion.getPackageServerGroup())) {
					dbrpm.getServerGroups().remove(svg);
					break;
				}
			}
			
			// check if it's used somewhere
			for (ETemplate t : templates) {
				if (t.getPackageVersions().contains(dbrpm)) {
					found = true;
					break;
				}
			}
			
			if (dbrpm.getServerGroups().size() > 0) {
				// keep it since other mirrors still reference it
				dbrpm.setDeprecated(false);
				this.drpm.save(dbrpm);
				return true;
			} else if (found) {
				dbrpm.setDeprecated(true);
				this.drpm.save(dbrpm);
				return true;
			} else {
				// delete it
				this.drpm.deleteById(dbrpm.getId());
				return false;
			}
		}
		return deprecated;
	}
	
	private void autoUpdate(List<ETemplate> templates) {
		PackageVersionComparator rpmComp = new PackageVersionComparator();
		for (ETemplate t : templates) {
			if ((t.getAutoUpdate() == null) || !t.getAutoUpdate()) {
				continue;
			}
			List<EPackageVersion> list = new ArrayList<>(t.getPackageVersions());
			for (EPackageVersion rpm : t.getPackageVersions()) {
				List<EPackageVersion> eprpms = new ArrayList<>(rpm.getPkg().getRPMs());
				Collections.sort(eprpms, rpmComp);
				EPackageVersion newest = eprpms.get(eprpms.size() - 1);
				if (!newest.equals(rpm)) {
					list.remove(rpm);
					list.add(newest);
				}
			}
			t.setPackageVersions(list);
			this.dtemplate.save(t);
		}
	}
	
}
