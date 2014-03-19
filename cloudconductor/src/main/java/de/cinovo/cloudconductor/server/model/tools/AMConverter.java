package de.cinovo.cloudconductor.server.model.tools;

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

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.Package;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.SSHKey;
import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IDependencyDAO;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ESSHKey;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.ETemplate;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */

public class AMConverter {
	
	@Autowired
	private ISSHKeyDAO dssh;
	
	@Autowired
	private IFileDAO dcf;
	
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
	
	
	/**	 */
	public AMConverter() {
		// nothing to do, but prevent init;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public ETemplate toModel(Template api) {
		ETemplate model = new ETemplate();
		model.setName(api.getName());
		model.setDescription(api.getDescription());
		Pattern pattern = Pattern.compile("\\w*://.*");
		model.setYum(new EPackageServer());
		if (pattern.matcher(api.getYum()).matches()) {
			model.getYum().setPath(api.getYum());
		} else {
			model.getYum().setPath("http://" + api.getYum());
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
		return model;
	}
	
	/**
	 * @param api the api object
	 * @return the model object
	 */
	public EService toModel(Service api) {
		EService model = this.dservice.findByName(api.getName());
		if (model == null) {
			model = new EService();
			model.setName(api.getName());
		}
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
