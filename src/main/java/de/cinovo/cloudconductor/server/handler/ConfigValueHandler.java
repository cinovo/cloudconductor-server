package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.dao.hibernate.ConfigValueDAOHib;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.cinovo.cloudconductor.server.util.ReservedConfigKeyStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch, mweise
 */
@Service
public class ConfigValueHandler {
	
	private static final int MAX_VAR_REPLACEMENTS = 10;

	private IConfigValueDAO configValueDAO;
	
	/**
	 * @param configValueDAO	DAO to use
	 */
	@Autowired
	public ConfigValueHandler(IConfigValueDAO configValueDAO) {
		this.configValueDAO = configValueDAO;
	}
	
	/**
	 * @param template	name of the template
	 * @return configuration values for given template with variables replaced
	 */
	@Transactional
	public ConfigValue[] get(String template) {
		ConfigValue[] clean = this.getClean(template);
		return this.swapVariables(template, clean);
	}
	
	/**
	 * @param template name of the template
	 * @return configuration values for given template without variables replaced
	 */
	public ConfigValue[] getClean(String template) {
		SortedMap<String, ConfigValue> result = new TreeMap<>();
		if (!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_VARIABLE)) {
			for (ConfigValue reservedCV : ReservedConfigKeyStore.instance.getReservedAsConfigValue()) {
				result.put(reservedCV.getKey(), reservedCV);
			}
			for (EConfigValue ecv : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL)) {
				result.put(ecv.getConfigkey(), ecv.toApi());
			}
		}
		
		// override with template specific configuration
		if (!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			for (EConfigValue ecv : this.configValueDAO.findBy(template)) {
				result.put(ecv.getConfigkey(), ecv.toApi());
			}
		}

		return result.values().toArray(new ConfigValue[0]);
	}
	
	/**
	 * @param template	the name of the template
	 * @return all configuration values for given template without stacking and without variables replaced
	 */
	public ConfigValue[] getCleanUnstacked(String template) {
		List<ConfigValue> result = new ArrayList<>();
		for (EConfigValue ecv : this.configValueDAO.findAll(template)) {
			result.add(ecv.toApi());
		}
		return result.toArray(new ConfigValue[0]);
	}
	
	/**
	 * @param template	name of the template
	 * @param service	name of the service
	 * @return configuration value for given service and template with variables replaced
	 */
	public ConfigValue[] get(String template, String service) {
		ConfigValue[] cleanCVs = this.getClean(template, service);
		return this.swapVariables(template, cleanCVs);
	}
	
	/**
	 * @param template	name of the template
	 * @param service	name of the service
	 * @return configuration values for given template and service without variables replaced
	 */
	public ConfigValue[] getClean(String template, String service) {
		SortedMap<String, ConfigValue> cvMap = new TreeMap<>();
		if (!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_VARIABLE)) {
			for (ConfigValue reservedKV : ReservedConfigKeyStore.instance.getReservedAsConfigValue()) {
				cvMap.put(reservedKV.getKey(), reservedKV);
			}
			for (ConfigValue globalCV : this.get(ConfigValueDAOHib.RESERVED_GLOBAL)) {
				cvMap.put(globalCV.getKey(), globalCV);
			}
			for (EConfigValue ecv : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL, service)) {
				cvMap.put(ecv.getConfigkey(), ecv.toApi());
			}
		}
		
		// override with template specific configs
		if (!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			for (ConfigValue cv : this.get(template)){
				cvMap.put(cv.getKey(), cv);
			}
			for (EConfigValue ecv : this.configValueDAO.findBy(template, service)) {
				cvMap.put(ecv.getConfigkey(), ecv.toApi());
			}
		}
		return cvMap.values().toArray(new ConfigValue[0]);
	}
	
	/**
	 * @param template	name of the template
	 * @param service	name of the service
	 * @param key		configuration key
	 * @return configuration value for given key under specific template and service with variables replaced
	 */
	public String get(String template, String service, String key) {
		String cleanValue = this.getClean(template, service, key);
		return this.swapVariables(template, cleanValue);
	}
	
	/**
	 * @param template	name of the template
	 * @param service	name of the service
	 * @param key		configuration key
	 * @return configuration value for given key under specific template and service without variables replaced
	 */
	public String getClean(String template, String service, String key) {
		EConfigValue result;
		if(ReservedConfigKeyStore.instance.isReserved(key)) {
			return ReservedConfigKeyStore.instance.getValue(key);
		}
		result = this.configValueDAO.findBy(template, service, key);
		if(result == null) {
			result = this.configValueDAO.findBy(template, null, key);
		}
		if(result == null) {
			result = this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL, service, key);
		}
		if(result == null) {
			result = this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL, null, key);
		}
		if(result == null) {
			throw new NotFoundException();
		}
		return result.getValue();
	}
	
	private ConfigValue[] swapVariables(String template, ConfigValue[] configs) {
		Set<EConfigValue> variables = this.getVariables(template);
		for(ConfigValue configValue : configs) {
			configValue.setValue(this.swapVariables(variables, String.valueOf(configValue.getValue()), 0));
		}
		return configs;
	}

	private String swapVariables(String template, String originalValue) {
		return this.swapVariables(this.getVariables(template), originalValue, 0);
	}
	
	private String swapVariables(Set<EConfigValue> availableVariables, String currentValue, int replacements) {
		for (EConfigValue variable : availableVariables) {
			String replacedValue = currentValue.replace(variable.getConfigkey(), variable.getValue());
			if (replacedValue.equals(currentValue)) {
				continue; // no match, try next variable
			}
			
			replacements++;
			if (replacements < ConfigValueHandler.MAX_VAR_REPLACEMENTS) {
				return this.swapVariables(availableVariables, replacedValue, replacements); // go deeper
			}
			return replacedValue; // stop replacing
		}
		return currentValue; // no more replacements found
	}

	private Set<EConfigValue> getVariables(String template) {
		Set<EConfigValue> variables = new LinkedHashSet<>(this.configValueDAO.findBy(template, ConfigValueDAOHib.RESERVED_VARIABLE));
		for(EConfigValue var : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_VARIABLE)) {
			if(variables.stream().noneMatch((v) -> v.getConfigkey().equals(var.getConfigkey()))) {
				variables.add(var);
			}
		}
		return variables;
	}
}
