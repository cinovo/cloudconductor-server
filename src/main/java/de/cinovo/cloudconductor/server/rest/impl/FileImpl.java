package de.cinovo.cloudconductor.server.rest.impl;

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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IFile;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileDataDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class FileImpl extends ImplHelper implements IFile {
	
	@Autowired
	private IFileDAO dcf;
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private IServiceDAO dservice;
	@Autowired
	private AMConverter amc;
	@Autowired
	private IFileDataDAO dcfd;
	@Autowired
	private ITemplateDAO dtemplate;


	@Override
	@Transactional
	public ConfigFile[] get() {
		Set<ConfigFile> result = new HashSet<>();
		for (EFile m : this.dcf.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new ConfigFile[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(String name, ConfigFile configFile) {
		this.assertName(name, configFile);
		EFile cf = this.amc.toModel(configFile);
		
		if ((configFile.getPkg() != null) && !configFile.getPkg().isEmpty()) {
			EPackage pkg = this.findByName(this.dpkg, configFile.getPkg());
			cf.setPkg(pkg);
		} else {
			cf.setPkg(null);
		}
		
		if ((configFile.getDependentServices() != null) && !configFile.getDependentServices().isEmpty()) {
			List<EService> services = this.findByName(this.dservice, configFile.getDependentServices());
			cf.setDependentServices(services);
		} else {
			cf.setDependentServices(null);
		}
		
		this.dcf.save(cf);
	}
	
	@Override
	@Transactional
	public ConfigFile get(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.findByName(this.dcf, name);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public String getData(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.findByName(this.dcf, name);
		EFileData data = this.dcfd.findDataByFile(model);
		return data.getData();
	}
	
	@Override
	@Transactional
	public void saveData(String name, String data) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(data);
		EFile model = this.findByName(this.dcf, name);
		try {
			byte[] array = MessageDigest.getInstance("MD5").digest(data.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			model.setChecksum(sb.toString());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// should never happen, if it does-> leave checksum empty
		}
		model = this.dcf.save(model);
		EFileData edata = this.dcfd.findDataByFile(model);
		if (edata == null) {
			edata = new EFileData();
		}
		edata.setData(data);
		edata.setParent(model);
		this.dcfd.save(edata);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.dcf.findByName(name);
		this.assertModelFound(model);
		this.dcf.delete(model);
	}

	@Override
	@Transactional
	public ConfigFile[] getConfigFiles(String template) {
		RESTAssert.assertNotEmpty(template);
		Set<ConfigFile> result = new HashSet<>();
		ETemplate eTemplate = this.dtemplate.findByName(template);
		if ((eTemplate != null) && (eTemplate.getConfigFiles() != null)) {
			for (EFile m : eTemplate.getConfigFiles()) {
				result.add(MAConverter.fromModel(m));
			}
		}
		return result.toArray(new ConfigFile[result.size()]);
	}
	
}
