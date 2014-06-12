package de.cinovo.cloudconductor.server.web.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import de.cinovo.cloudconductor.server.comparators.ConfigComperator;
import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.interfaces.IConfig;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class ConfigImpl extends AWebPage implements IConfig {
	
	@Autowired
	protected IConfigValueDAO dConfig;
	
	
	@Override
	protected String getTemplateFolder() {
		return "config";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IConfig.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IConfig.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IConfig.ROOT + IWebPath.ACTION_ADD, "Create new Key");
		this.addTopAction(IWebPath.WEBROOT + IConfig.ROOT + IConfig.BATCH_ACTION, "Batch modify");
		for (String template : this.dConfig.findTemplates()) {
			if (template.equals(IConfig.RESERVED_GLOBAL)) {
				this.addViewType(template, template, true);
			} else {
				this.addViewType(this.templateNormer(template), template, false);
			}
		}
	}
	
	private String templateNormer(String template) {
		return template.trim().replace(" ", "_");
	}
	
	@Override
	protected String getNavElementName() {
		return "Config";
	}
	
	@Override
	@Transactional
	public RenderedView view(String viewtype) {
		String template = viewtype == null ? IConfig.RESERVED_GLOBAL : viewtype;
		
		List<EConfigValue> configs = this.dConfig.findAll(template);
		Map<String, List<EConfigValue>> conf = new HashMap<String, List<EConfigValue>>();
		for (EConfigValue val : configs) {
			String service = val.getService();
			if ((service != null) && service.isEmpty()) {
				service = null;
			}
			if (!conf.containsKey(service)) {
				conf.put(service, new ArrayList<EConfigValue>());
			}
			conf.get(service).add(val);
		}
		ConfigComperator comperator = new ConfigComperator();
		for (Entry<String, List<EConfigValue>> a : conf.entrySet()) {
			Collections.sort(a.getValue(), comperator);
		}
		CSViewModel view = this.createView();
		view.addModel("CONFIGS", this.sortMap(conf));
		view.addModel("TEMPLATE", template);
		return view.render();
	}
	
	@Override
	public RenderedView deleteConfigView(String id) {
		RESTAssert.assertNotEmpty(id);
		EConfigValue config = this.dConfig.findById(Long.valueOf(id));
		RESTAssert.assertNotNull(config);
		CSViewModel modal = this.createModal("mDeleteConfig");
		modal.addModel("CONFIG", config);
		return modal.render();
	}
	
	@Override
	public RenderedView deleteTemplateView(String template) {
		RESTAssert.assertNotNull(template);
		CSViewModel modal = this.createModal("mDeleteConfig");
		modal.addModel("TEMPLATE", template);
		return modal.render();
	}
	
	@Override
	public RenderedView deleteServiceView(String template, String service) {
		RESTAssert.assertNotNull(template);
		CSViewModel modal = this.createModal("mDeleteConfig");
		modal.addModel("TEMPLATE", template);
		modal.addModel("SERVICE", service);
		return modal.render();
	}
	
	@Override
	public RenderedView editConfigView(String id) {
		RESTAssert.assertNotEmpty(id);
		EConfigValue config = this.dConfig.findById(Long.valueOf(id));
		RESTAssert.assertNotNull(config);
		CSViewModel modal = this.createModal("mModConfig");
		modal.addModel("CONFIG", config);
		return modal.render();
	}
	
	@Override
	public RenderedView addConfigView() {
		CSViewModel modal = this.createModal("mModConfig");
		return modal.render();
	}
	
	@Override
	public RenderedView addConfigView(String template) {
		RESTAssert.assertNotNull(template);
		CSViewModel modal = this.createModal("mModConfig");
		modal.addModel("TEMPLATE", template);
		return modal.render();
	}
	
	@Override
	public RenderedView addConfigView(String template, String service) {
		RESTAssert.assertNotNull(template);
		RESTAssert.assertNotNull(service);
		CSViewModel modal = this.createModal("mModConfig");
		modal.addModel("TEMPLATE", template);
		modal.addModel("SERVICE", service);
		return modal.render();
	}
	
	@Override
	public RenderedView batchModView() {
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
		CSViewModel modal = this.createModal("mBatchMod");
		modal.addModel("BATCHDEFAULT", buffer.toString());
		return modal.render();
	}
	
	@Override
	public AjaxAnswer save(String oldId, String template, String service, String key, String value) throws FormErrorException {
		FormErrorException error = null;
		error = this.assertNotEmpty(template, error, "template");
		error = this.assertNotEmpty(key, error, "key");
		error = this.assertNotEmpty(value, error, "value");
		EConfigValue config = (oldId == null) || (Long.valueOf(oldId) < 1) ? null : this.dConfig.findById(Long.valueOf(oldId));
		if ((config == null) || !config.getConfigkey().equals(key)) {
			if ((service != null) && !service.isEmpty() && (this.dConfig.findBy(template, service, key) != null)) {
				error = error == null ? this.createError("The key already exists.") : error;
				error.addElementError("key", true);
			} else if (this.dConfig.findKey(template, key) != null) {
				error = error == null ? this.createError("The key already exists.") : error;
				error.addElementError("key", true);
			}
		}
		if (error != null) {
			error.addFormParam("oldId", oldId);
			error.addFormParam("template", template);
			error.addFormParam("service", service);
			error.addFormParam("key", key);
			error.addFormParam("value", value);
			if ((oldId != null) && oldId.equals("0")) {
				error.setParentUrl(IConfig.ROOT, IWebPath.ACTION_ADD);
			} else {
				error.setParentUrl(IConfig.ROOT, oldId, IWebPath.ACTION_EDIT);
			}
			throw error;
		}
		
		if (config == null) {
			config = new EConfigValue();
		}
		
		config.setTemplate(template);
		config.setService((service == null) || service.isEmpty() ? null : service);
		config.setConfigkey(key);
		config.setValue(value);
		this.dConfig.save(config);
		this.addViewType(template, template, false);
		return new AjaxAnswer(IWebPath.WEBROOT + IConfig.ROOT, template);
	}
	
	@Override
	public AjaxAnswer deleteConfig(String id) {
		RESTAssert.assertNotEmpty(id);
		EConfigValue config = this.dConfig.findById(Long.valueOf(id));
		RESTAssert.assertNotNull(config);
		this.dConfig.delete(config);
		this.audit("Removed config " + config.getConfigkey() + " from template " + config.getTemplate() + " and service " + config.getService());
		return new AjaxAnswer(IWebPath.WEBROOT + IConfig.ROOT, config.getTemplate());
	}
	
	@Override
	public AjaxAnswer deleteTemplate(String template) {
		RESTAssert.assertNotEmpty(template);
		List<EConfigValue> found = this.dConfig.findAll(template);
		for (EConfigValue cv : found) {
			this.dConfig.delete(cv);
		}
		this.removeViewType(template);
		this.audit("Removed config for template " + template);
		return new AjaxAnswer(IWebPath.WEBROOT + IConfig.ROOT, IConfig.RESERVED_GLOBAL);
	}
	
	@Override
	public AjaxAnswer deleteService(String template, String service) {
		RESTAssert.assertNotEmpty(template);
		RESTAssert.assertNotEmpty(service);
		List<EConfigValue> found = this.dConfig.findBy(template, service);
		for (EConfigValue cv : found) {
			this.dConfig.delete(cv);
		}
		this.audit("Removed config for service " + service + " of template " + template);
		return new AjaxAnswer(IWebPath.WEBROOT + IConfig.ROOT);
	}
	
	@Override
	public AjaxAnswer batchMod(String batch) {
		RESTAssert.assertNotEmpty(batch);
		Map<String, String> drop = new HashMap<>();
		Table<String, String, Set<String>> remove = HashBasedTable.create();
		Table<String, String, Map<String, String>> add = HashBasedTable.create();
		try (Scanner sc = new Scanner(batch)) {
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
		return new AjaxAnswer(IWebPath.WEBROOT + IConfig.ROOT);
	}
}
