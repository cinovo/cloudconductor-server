package de.cinovo.cloudconductor.server.rest.shared;

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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.interfaces.IFile;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileDataDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.FileHandler;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EFileData;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class FileImpl implements IFile {
	
	@Autowired
	private IFileDAO fileDAO;
	@Autowired
	private IFileDataDAO fileDataDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private FileHandler fileHandler;
	
	
	@Override
	@Transactional
	public ConfigFile[] get() {
		Set<ConfigFile> result = new HashSet<>();
		for (EFile m : this.fileDAO.findList()) {
			result.add(m.toApi());
		}
		return result.toArray(new ConfigFile[result.size()]);
	}
	
	@Override
	@Transactional
	public void save(ConfigFile configFile) {
		RESTAssert.assertNotNull(configFile);
		RESTAssert.assertNotNull(configFile.getName());
		
		EFile efile = this.fileDAO.findByName(configFile.getName());
		if (efile == null) {
			this.fileHandler.createEntity(configFile);
		} else {
			this.fileHandler.updateEntity(efile, configFile);
		}
	}
	
	@Override
	@Transactional
	public ConfigFile get(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.fileDAO.findByName(name);
		RESTAssert.assertNotNull(model, Response.Status.NOT_FOUND);
		return model.toApi();
	}
	
	@Override
	@Transactional
	public String getData(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.fileDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		EFileData data = this.fileDataDAO.findDataByFile(model);
		if (data != null) {
			return data.getData();
		}
		return "";
	}
	
	@Override
	@Transactional
	public void saveData(String name, String data) {
		RESTAssert.assertNotEmpty(name);
		RESTAssert.assertNotEmpty(data);
		
		EFile model = this.fileDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		
		model.setChecksum(this.fileHandler.createChecksum(data));
		model = this.fileDAO.save(model);
		
		EFileData edata = this.fileDataDAO.findDataByFile(model);
		if (edata == null) {
			this.fileHandler.createEntity(model, data);
		} else {
			this.fileHandler.updateEntity(edata, data);
		}
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EFile model = this.fileDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		this.fileDAO.delete(model);
	}
	
	@Override
	@Transactional
	public ConfigFile[] getConfigFiles(String template) {
		RESTAssert.assertNotEmpty(template);
		Set<ConfigFile> result = new HashSet<>();
		ETemplate eTemplate = this.templateDAO.findByName(template);
		if ((eTemplate != null) && (eTemplate.getConfigFiles() != null)) {
			for (EFile m : eTemplate.getConfigFiles()) {
				result.add(m.toApi());
			}
		}
		return result.toArray(new ConfigFile[result.size()]);
	}
	
}
