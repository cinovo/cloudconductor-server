package de.cinovo.cloudconductor.server.web.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.cinovo.cloudconductor.server.dao.IAuditLogDAO;
import de.cinovo.cloudconductor.server.model.EAuditLog;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.interfaces.IAudit;

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

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author astifel
 * 
 */
public class AuditImpl extends AWebPage implements IAudit {
	
	@Autowired
	private IAuditLogDAO dAuditLog;
	
	
	@Override
	protected String getTemplateFolder() {
		return "audit";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IAudit.ROOT);
	}
	
	@Override
	protected String getNavElementName() {
		return "Audit";
	}
	
	@Override
	public RenderedView view(String range) {
		// Build audits model
		List<EAuditLog> audits = this.dAuditLog.findList();
		
		final CSViewModel vm = this.createView();
		audits = Lists.reverse(audits);
		vm.addModel("audits", audits);
		System.out.println(range);
		// Fill template with models and return
		
		return vm.render();
	}
	
}
