package de.cinovo.cloudconductor.server.impl.web;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.helper.ReportPackage;
import de.cinovo.cloudconductor.server.web.interfaces.IReport;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ReportImpl extends AbstractWebImpl implements IReport {
	
	@Override
	protected String getTemplateFolder() {
		return "report";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Reports";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IReport.ROOT;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		// Build hosts model.
		List<ETemplate> templates = this.dTemplate.findList();
		
		Set<EPackageVersion> installedPackages = Sets.newHashSet();
		
		for (ETemplate temp : templates) {
			for (EPackageVersion rpm : temp.getPackageVersions()) {
				installedPackages.add(rpm);
			}
		}
		
		List<ReportPackage> packagesModel = new ArrayList<>();
		for (EPackageVersion pkg : installedPackages) {
			// Add package model to packages model.
			ReportPackage packageModel = new ReportPackage(pkg.getPkg().getName(), pkg.getVersion());
			packagesModel.add(packageModel);
		}
		Collections.sort(packagesModel);
		
		// Fill template with models and return.
		final ViewModel vm = this.createView();
		vm.addModel("packages", packagesModel);
		return vm;
	}
}
