package de.cinovo.cloudconductor.server.web.impl;

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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.ReportPackage;
import de.cinovo.cloudconductor.server.rest.impl.ReportImpl;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.interfaces.IReport;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class WebReportImpl extends AWebPage implements IReport {
	
	@Autowired
	private ReportImpl restImpl;
	
	
	@Override
	protected String getTemplateFolder() {
		return "report";
	}
	
	@Override
	protected void init() {
		// nothing to do
	}
	
	@Override
	protected String getNavElementName() {
		return "Report";
	}
	
	@Override
	@Transactional
	public RenderedView view() {
		List<ReportPackage> packagesModel = this.restImpl.getReportInformation();
		
		// Fill template with models and return.
		final CSViewModel vm = this.createView();
		vm.addModel("packages", packagesModel);
		return vm.render();
	}
}
