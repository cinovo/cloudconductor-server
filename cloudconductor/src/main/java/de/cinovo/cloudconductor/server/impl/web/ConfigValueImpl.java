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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.ws.rs.core.Response;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import de.cinovo.cloudconductor.api.model.KeyValue;
import de.cinovo.cloudconductor.server.model.config.EConfigValue;
import de.cinovo.cloudconductor.server.web.interfaces.IConfig;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigValueImpl extends AbstractWebImpl implements IConfig {
	
	@Override
	protected String getTemplateFolder() {
		return "config";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Configurations";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IConfig.ROOT;
	}
	
	@Override
	@Transactional
	public Response view() {
		return this.redirect(IConfig.RESERVED_GLOBAL);
	}
	
	@Override
	@Transactional
	public ViewModel view(String template) {
		RESTAssert.assertNotEmpty(template);
		
		List<EConfigValue> ctemplate = this.dConfig.findAll(template);
		
		Map<String, Object> currentTemplate = new HashMap<>();
		currentTemplate.put("name", template);
		Map<String, ArrayList<KeyValue>> services = new TreeMap<String, ArrayList<KeyValue>>();
		
		for (EConfigValue config : ctemplate) {
			if (!services.containsKey(config.getService() == null ? "" : config.getService())) {
				services.put(config.getService() == null ? "" : config.getService(), new ArrayList<KeyValue>());
			}
			services.get(config.getService() == null ? "" : config.getService()).add(new KeyValue(config.getConfigkey(), config.getValue()));
			Collections.sort(services.get(config.getService() == null ? "" : config.getService()));
		}
		currentTemplate.put("services", services);
		
		final ViewModel vm = this.createView();
		vm.addModel("templates", this.getTemplates());
		vm.addModel("currentTemplate", currentTemplate);
		return vm;
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd() {
		return this.editMask("", "", "", "");
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd(String template) {
		return this.editMask(template, "", "", "");
	}
	
	@Override
	@Transactional
	public ViewModel viewAdd(String template, String service) {
		return this.editMask(template, service, "", "");
	}
	
	@Override
	@Transactional
	public ViewModel viewEdit(String template, String service, String key) {
		EConfigValue found = this.dConfig.findBy(template, service, key);
		RESTAssert.assertNotNull(found);
		return this.editMask(found.getTemplate(), found.getService(), found.getConfigkey(), found.getValue());
	}
	
	@Override
	@Transactional
	public ViewModel viewEdit(String template, String key) {
		EConfigValue found = this.dConfig.findKey(template, key);
		RESTAssert.assertNotNull(found);
		return this.editMask(found.getTemplate(), found.getService(), found.getConfigkey(), found.getValue());
	}
	
	@Override
	@Transactional
	public Response delete(String template, String key) {
		EConfigValue found = this.dConfig.findKey(template, key);
		RESTAssert.assertNotNull(found);
		this.dConfig.delete(found);
		return this.redirect(template);
	}
	
	@Override
	@Transactional
	public Response delete(String template) {
		List<EConfigValue> found = this.dConfig.findAll(template);
		for (EConfigValue cv : found) {
			this.dConfig.delete(cv);
		}
		return this.redirect(template);
	}
	
	@Override
	@Transactional
	public Response delete(String template, String service, String key) {
		EConfigValue found = this.dConfig.findBy(template, service, key);
		RESTAssert.assertNotNull(found);
		this.dConfig.delete(found);
		return this.redirect(template);
	}
	
	@Override
	@Transactional
	public Object save(String template, String service, String key, String ftemplate, String fservice, String fkey, String fvalue) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		RESTAssert.assertNotEmpty(key);
		EConfigValue found = this.dConfig.findBy(template, service, key);
		String error = this.save(found, ftemplate, fservice, fkey, fvalue, true);
		if (error != null) {
			ViewModel vm = this.editMask(ftemplate, fservice, fkey, fvalue);
			vm.addModel("ptemplate", template);
			vm.addModel("pservice", service);
			vm.addModel("pkey", key);
			vm.addModel("error", error);
			return vm;
		}
		return this.redirect(template);
	}
	
	@Override
	@Transactional
	public Object save(String template, String key, String ftemplate, String fservice, String fkey, String fvalue) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(key);
		EConfigValue found = this.dConfig.findKey(template, key);
		String error = this.save(found, ftemplate, fservice, fkey, fvalue, true);
		if (error != null) {
			ViewModel vm = this.editMask(ftemplate, fservice, fkey, fvalue);
			vm.addModel("ptemplate", template);
			vm.addModel("pservice", null);
			vm.addModel("pkey", key);
			vm.addModel("error", error);
			return vm;
		}
		return this.redirect(template);
	}
	
	@Override
	@Transactional
	public Object save(String ftemplate, String fservice, String fkey, String fvalue) {
		EConfigValue found = null;
		if (fservice.isEmpty()) {
			found = this.dConfig.findKey(ftemplate, fkey);
		} else {
			found = this.dConfig.findBy(ftemplate, fservice, fkey);
		}
		String error = this.save(found, ftemplate, fservice, fkey, fvalue, false);
		if (error != null) {
			ViewModel vm = this.editMask(ftemplate, fservice, fkey, fvalue);
			vm.addModel("ptemplate", null);
			vm.addModel("pservice", null);
			vm.addModel("pkey", null);
			vm.addModel("error", error);
			return vm;
		}
		return this.redirect();
	}
	
	@Override
	@Transactional
	public ViewModel viewRestore() {
		List<EConfigValue> total = this.dConfig.findList();
		StringBuffer buffer = new StringBuffer();
		for (EConfigValue value : total) {
			buffer.append("#+/");
			buffer.append(value.getTemplate());
			buffer.append("/");
			buffer.append(value.getService());
			buffer.append("/");
			buffer.append(value.getConfigkey());
			buffer.append("=");
			buffer.append(value.getValue());
			buffer.append(System.lineSeparator());
		}
		final ViewModel vm = this.createView("confirmRestore");
		vm.addModel("defaultList", buffer.toString());
		return vm;
	}
	
	@Override
	@Transactional
	public Response restore(String restore) {
		RESTAssert.assertNotEmpty(restore);
		Map<String, String> drop = new HashMap<>();
		Table<String, String, Set<String>> remove = HashBasedTable.create();
		Table<String, String, Map<String, String>> add = HashBasedTable.create();
		try (Scanner sc = new Scanner(restore)) {
			while (sc.hasNextLine()) {
				String[] line = sc.nextLine().split("/", 4);
				if ((line == null) || (line.length < 2) || line[0].startsWith("#")) {
					continue;
				}
				if (line.length < 3) {
					String[] tmp = new String[3];
					tmp[0] = line[0];
					tmp[1] = line[1];
					tmp[2] = "";
					line = tmp;
				}
				String[] pair = new String[2];
				if ((line.length >= 4) && !line[3].isEmpty()) {
					if (line[3].contains("=")) {
						pair = line[3].split("=", 2);
					} else {
						pair[0] = line[3];
					}
				}
				switch (line[0]) {
				case "DROP":
					drop.put(line[1], line[2]);
					break;
				case "-":
					if ((pair[0] == null) || pair[0].isEmpty()) {
						drop.put(line[1], line[2]);
						continue;
					}
					if (remove.get(line[1], line[2]) == null) {
						remove.put(line[1], line[2], new HashSet<String>());
					}
					remove.get(line[1], line[2]).add(pair[0]);
					break;
				case "+":
					if (add.get(line[1], line[2]) == null) {
						add.put(line[1], line[2], new HashMap<String, String>());
					}
					add.get(line[1], line[2]).put(pair[0], pair[1]);
					break;
				default:
					break;
				}
			}
		}
		
		// drop
		for (Entry<String, String> entry : drop.entrySet()) {
			for (EConfigValue ecv : this.dConfig.findBy(entry.getKey(), entry.getValue())) {
				this.dConfig.delete(ecv);
			}
			
		}
		// delete
		for (Cell<String, String, Set<String>> entry : remove.cellSet()) {
			for (EConfigValue ecv : this.dConfig.findBy(entry.getRowKey(), entry.getColumnKey())) {
				if (entry.getValue().contains(ecv.getConfigkey())) {
					this.dConfig.delete(ecv);
				}
			}
			
		}
		// add
		for (Cell<String, String, Map<String, String>> entry : add.cellSet()) {
			for (Entry<String, String> kv : entry.getValue().entrySet()) {
				EConfigValue ecv = this.dConfig.findBy(entry.getRowKey(), entry.getColumnKey(), kv.getKey());
				if (ecv == null) {
					ecv = new EConfigValue();
					ecv.setTemplate(entry.getRowKey());
					ecv.setService(entry.getColumnKey());
					ecv.setConfigkey(kv.getKey());
				}
				ecv.setValue(kv.getValue());
				this.dConfig.save(ecv);
			}
		}
		return this.redirect();
	}
	
	private String save(EConfigValue found, String template, String service, String key, String value, boolean edit) {
		if ((template == null) || template.isEmpty()) {
			return "The template may not be empty";
		}
		if ((key == null) || key.isEmpty()) {
			return "the key may not be empty";
		}
		
		if ((found != null) && !edit) {
			return "The given key already exists";
		} else if (((found != null) && !found.getConfigkey().equals(key))) {
			EConfigValue check = null;
			if ((found.getService() == null) || found.getService().isEmpty()) {
				check = this.dConfig.findKey(found.getTemplate(), key);
			} else {
				check = this.dConfig.findBy(found.getTemplate(), found.getService(), key);
			}
			if (check != null) {
				return "The given key already exists";
			}
		}
		
		EConfigValue sfound = found;
		if (sfound == null) {
			sfound = new EConfigValue();
		}
		
		sfound.setTemplate(template);
		sfound.setService((service == null) || service.isEmpty() ? null : service);
		sfound.setConfigkey(key);
		sfound.setValue(value);
		this.dConfig.save(sfound);
		return null;
	}
	
	private ViewModel editMask(String template, String service, String key, String value) {
		final ViewModel vm = this.createView("confirmSave");
		vm.addModel("ptemplate", (template == null) || template.isEmpty() ? null : template);
		vm.addModel("pservice", (service == null) || service.isEmpty() ? null : service);
		vm.addModel("pkey", (key == null) || key.isEmpty() ? null : key);
		
		vm.addModel("template", template);
		vm.addModel("service", service);
		vm.addModel("key", key);
		vm.addModel("value", value);
		return vm;
	}
	
	private List<String> getTemplates() {
		List<String> result = new ArrayList<>();
		for (EConfigValue config : this.dConfig.findList()) {
			if (!result.contains(config.getTemplate())) {
				result.add(config.getTemplate());
			}
		}
		Collections.sort(result, new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				if (o1.equals("GLOBAL")) {
					return -1;
				}
				if (o2.equals("GLOBAL")) {
					return 1;
				}
				return o1.compareTo(o2);
			}
			
		});
		return result;
	}
}
