package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.dao.hibernate.ConfigValueDAOHib;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Copyright 2019 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class GlobalConfigMigrator {
	
	private final Logger logger = LoggerFactory.getLogger(GlobalConfigMigrator.class);
	private final String globalVarPrefix = "global";
	
	@Autowired
	private IConfigValueDAO cvDAO;
	
	/**
	 * Migrate all templates from global configs to variables
	 */
	public void migrateGlobalConfig() {
		this.logger.info("Start migrating global configurations to variables...");
		
		Map<String, String> globalConfigMap = this.migrateGlobalTemplate();
		int templates = 0;
		for (String template : this.cvDAO.findRealTemplates()) {
			Map<String, String> configMap = this.migrateGlobalServiceOfTemplate(globalConfigMap, template);
			
			for (String service : this.cvDAO.findServicesForTemplate(template)) {
				for (Entry<String, String> entry : configMap.entrySet()) {
					String key = entry.getKey();
					if (this.cvDAO.findBy(template, service, key) != null) {
						continue;
					}
					this.cvDAO.save(new EConfigValue(template, service, key, entry.getValue()));
				}
			}
			templates++;
		}
		
		this.logger.info("Migrated global configurations of {} templates.", templates);
	}
	
	private Map<String, String> migrateGlobalTemplate() {
		Map<String, String> globalConfigMap = new HashMap<>();
		List<EConfigValue> globalTemplateConfigs = this.cvDAO.findForGlobalTemplate();
		for (EConfigValue globalTemplateConfig : globalTemplateConfigs) {
			String originalKey = globalTemplateConfig.getConfigkey();
			String varKey = this.getVarKey(originalKey);
			globalTemplateConfig.setTemplate(ConfigValueDAOHib.RESERVED_VARIABLE);
			globalTemplateConfig.setConfigkey(varKey);
			this.cvDAO.save(globalTemplateConfig);
			globalConfigMap.put(originalKey, varKey);
		}
		
		this.logger.info("Migrated {} configuration values for template '{}'", globalConfigMap.size(), ConfigValueDAOHib.RESERVED_GLOBAL);
		return globalConfigMap;
	}
	
	private Map<String, String> migrateGlobalServiceOfTemplate(Map<String, String> globalConfigMap, String template) {
		Map<String, String> templateConfigMap = new HashMap<>(globalConfigMap);
		for (EConfigValue globalServiceConfig : this.cvDAO.findForGlobalService(template)) {
			String configKey = globalServiceConfig.getConfigkey();
			String varKey = this.getVarKey(configKey);
			globalServiceConfig.setService(ConfigValueDAOHib.RESERVED_VARIABLE);
			globalServiceConfig.setConfigkey(varKey);
			this.cvDAO.save(globalServiceConfig);
			templateConfigMap.put(configKey, varKey);
		}
		
		this.logger.info("Migrated {} configuration values for template '{}'", templateConfigMap.size(), template);
		return templateConfigMap;
	}
	
	private String getVarKey(String configKey) {
		return "${" + this.globalVarPrefix + "." + configKey + "}";
	}
	
}
