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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.interfaces.IPackageServer;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */

public class PackageServerImpl extends AbstractWebImpl implements IPackageServer {
	
	@Override
	protected String getTemplateFolder() {
		return "yumserver";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Files";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IPackageServer.ROOT;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EPackageServer> servers = this.dPackageServer.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		for (EPackageServer server : servers) {
			Map<String, Object> keymap = new HashMap<>();
			keymap.put("id", server.getId());
			keymap.put("path", server.getPath());
			keymap.put("description", (server.getDescription() == null) || server.getDescription().isEmpty() ? "no description" : server.getDescription());
			result.add(keymap);
		}
		final ViewModel vm = this.createView();
		vm.addModel("servers", result);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd() {
		Map<String, Object> result = new HashMap<>();
		result.put("id", "-1");
		result.put("path", "");
		result.put("description", "");
		final ViewModel vm = this.createView("save");
		vm.addModel("server", result);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewEdit(Long serverid) {
		EPackageServer server = this.dPackageServer.findById(serverid);
		Map<String, Object> result = new HashMap<>();
		result.put("id", server.getId());
		result.put("path", server.getPath());
		result.put("description", server.getDescription());
		final ViewModel vm = this.createView("save");
		vm.addModel("server", result);
		return vm;
	}
	
	@Override
	@Transactional
	public Object save(Long serverid, String path, String description) {
		RESTAssert.assertNotNull(serverid);
		String error = null;
		if (path.isEmpty()) {
			error = "Please enter a path.";
		}
		if (error != null) {
			Map<String, Object> result = new HashMap<>();
			result.put("id", serverid);
			result.put("path", path);
			result.put("description", description);
			final ViewModel vm = this.createView("save");
			vm.addModel("server", result);
			return vm;
		}
		
		EPackageServer server = this.dPackageServer.findById(serverid);
		if (server == null) {
			server = new EPackageServer();
		}
		server.setDescription(description);
		server.setPath(path);
		server = this.dPackageServer.save(server);
		return this.redirect(null, server.getId().toString());
	}
	
	@Override
	@Transactional
	public ViewModel viewDelete(Long serverid) {
		RESTAssert.assertNotNull(serverid);
		EPackageServer server = this.dPackageServer.findById(serverid);
		String msg = "Do you really want to remove the package server " + server.getPath() + "?";
		String header = "Remove package server";
		String back = "#" + serverid;
		return this.createDeleteView(header, msg, back, serverid.toString());
	}
	
	@Override
	@Transactional
	public Response delete(Long serverid) {
		RESTAssert.assertNotNull(serverid);
		EPackageServer server = this.dPackageServer.findById(serverid);
		List<ETemplate> tmplt = this.dTemplate.findByPackageServer(serverid);
		if ((tmplt != null) && (tmplt.size() > 0)) {
			// TODO Print some error
		} else {
			this.dPackageServer.delete(server);
		}
		return this.redirect();
	}
}
