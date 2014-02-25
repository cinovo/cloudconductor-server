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

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.interfaces.IServerOptions;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Path("/options")
public class ServerOptionsImpl extends AbstractWebImpl implements IServerOptions {
	
	@Override
	protected String getTemplateFolder() {
		return "options";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Options";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IServerOptions.ROOT;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		final ViewModel vm = this.createView();
		vm.addModel("options", this.dServerOptions.get());
		vm.addModel("links", this.dLinks.findList());
		return vm;
	}
	
	@Override
	@Transactional
	public Object save(String name, String bgcolor, String autoUpdate, String descr) {
		EServerOptions options = this.dServerOptions.get();
		if ((name != null) && !name.isEmpty()) {
			options.setName(name);
		}
		if ((bgcolor != null) && !bgcolor.isEmpty()) {
			options.setBgcolor(bgcolor);
		}
		options.setAllowautoupdate(autoUpdate == null ? false : true);
		options.setDescription(descr);
		this.dServerOptions.save(options);
		
		if (!options.isAllowautoupdate()) {
			for (ETemplate t : this.dTemplate.findList()) {
				if (t.getAutoUpdate()) {
					t.setAutoUpdate(false);
					this.dTemplate.save(t);
				}
			}
		}
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd() {
		return this.createView("addLabel");
	}
	
	@Override
	@Transactional
	public Object add(String label, String link) {
		String error = null;
		if ((label == null) || label.isEmpty()) {
			error = "The label mustn't be empty.";
		}
		if (error == null) {
			EAdditionalLinks additional = this.dLinks.findByLabel(label);
			if (additional != null) {
				error = "The label already exists.";
			}
		}
		
		if (error != null) {
			final ViewModel vm = this.createView("addLabel");
			vm.addModel("label", label);
			vm.addModel("link", link);
			vm.addModel("error", error);
			return vm;
		}
		EAdditionalLinks additional = new EAdditionalLinks();
		additional.setLabel(label);
		additional.setUrl(link);
		this.dLinks.save(additional);
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewRemove(String label) {
		RESTAssert.assertNotEmpty(label);
		String msg = "Do you really want to remove the additional link <b>" + label + "</b>?";
		String header = "Remove additional link: " + label;
		String back = "";
		return this.createDeleteView(header, msg, back, label);
	}
	
	@Override
	@Transactional
	public Response delete(String label) {
		RESTAssert.assertNotEmpty(label);
		EAdditionalLinks link = this.dLinks.findByLabel(label);
		if (link != null) {
			this.dLinks.delete(link);
		}
		return this.redirect();
	}
}
