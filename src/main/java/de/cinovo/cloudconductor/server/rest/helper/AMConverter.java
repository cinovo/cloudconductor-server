package de.cinovo.cloudconductor.server.rest.helper;

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

import de.cinovo.cloudconductor.api.model.*;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */

@Component
public class AMConverter {
	
	@Autowired
	private ISSHKeyDAO dssh;
	
	@Autowired
	private IFileDAO dcf;

	@Autowired
	private IDirectoryDAO ddir;
	
	@Autowired
	private IHostDAO dhost;
	
	@Autowired
	private IPackageDAO dpkg;
	
	@Autowired
	private IPackageVersionDAO drpm;
	
	@Autowired
	private IServiceDAO dservice;
	
	@Autowired
	private ITemplateDAO dtemplate;
	
	@Autowired
	private IDependencyDAO ddependendy;
	
	@Autowired
	private IPackageServerDAO packageServer;
	
	@Autowired
	private IPackageServerGroupDAO pvg;
	
	
	/**	 */
	public AMConverter() {
		// nothing to do
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public ETemplate toModel(Template api) {
		ETemplate model = new ETemplate();
		model.setName(api.getName());
		model.setDescription(api.getDescription());
		for (EPackageServer serv : this.packageServer.findList()) {
			if (api.getPackageServers().contains(serv.getPath()) || api.getPackageServers().contains(serv.getPath().substring(6))) {
				model.getPackageServers().add(serv);
				break;
			}
		}
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EFile toModel(ConfigFile api) {
		EFile model = this.dcf.findByName(api.getName());
		if (model == null) {
			model = new EFile();
			model.setName(api.getName());
		}
		model.setFileMode(api.getFileMode());
		model.setChecksum(api.getChecksum());
		model.setGroup(api.getGroup());
		model.setOwner(api.getOwner());
		model.setReloadable(api.isReloadable());
		model.setTemplate(api.isTemplate());
		model.setTargetPath(api.getTargetPath());
		model.setPkg(this.dpkg.findByName(api.getPkg()));
		return model;
	}

	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EDirectory toModel(Directory api){
		EDirectory model = this.ddir.findByName(api.getName());
		if (model == null){
			model = new EDirectory();
			model.setName(api.getName());
		}
		model.setFileMode(api.getFileMode());
		model.setGroup(api.getGroup());
		model.setOwner(api.getOwner());
		model.setTargetPath(api.getTargetPath());
		model.setPkg(this.dpkg.findByName(api.getPkg()));
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EDependency toModel(Dependency api) {
		EDependency model = this.ddependendy.find(api);
		if (model == null) {
			model = new EDependency();
			model.setName(api.getName());
			model.setOperator(api.getOperator());
			model.setType(api.getType());
			model.setVersion(api.getVersion());
		}
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EHost toModel(Host api) {
		EHost model = this.dhost.findByName(api.getName());
		if (model == null) {
			model = new EHost();
			model.setName(api.getName());
		}
		model.setDescription(api.getDescription());
		ETemplate template = this.dtemplate.findByName(api.getTemplate());
		if (template != null) {
			model.setTemplate(template);
		}
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EPackage toModel(Package api) {
		EPackage model = this.dpkg.findByName(api.getName());
		if (model == null) {
			model = new EPackage();
			model.setName(api.getName());
		}
		model.setDescription(api.getDescription());
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EPackageVersion toModel(PackageVersion api) {
		EPackageVersion model = this.drpm.find(api.getName(), api.getVersion());
		if (model == null) {
			model = new EPackageVersion();
			model.setVersion(api.getVersion());
		}
		EPackageServerGroup epsg = this.pvg.findByName(api.getPackageServerGroup());
		model.getServerGroups().add(epsg);
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EService toModel(Service api) {
		EService model = api.getId() == null ? null : this.dservice.findById(api.getId());
		if (model == null) {
			model = new EService();
		}
		model.setName(api.getName());
		model.setDescription(api.getDescription());
		model.setInitScript(api.getInitScript());
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public ESSHKey toModel(SSHKey api) {
		ESSHKey model = this.dssh.findByOwner(api.getOwner());
		if (model == null) {
			model = new ESSHKey();
			model.setOwner(api.getOwner());
		}
		model.setKeycontent(api.getKey());
		return model;
	}
	
}
