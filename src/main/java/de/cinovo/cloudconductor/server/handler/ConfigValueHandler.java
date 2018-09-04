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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class ConfigValueHandler {

	@Autowired
	private IConfigValueDAO configValueDAO;

	@Transactional
	public ConfigValue[] get(String template) {
		return this.swapVariables(template, this.getClean(template));
	}

	public ConfigValue[] getClean(String template) {
		Set<ConfigValue> result = new HashSet<>();
		for(EConfigValue ecv : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			result.add(ecv.toApi());
		}
		if(!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			for(EConfigValue ecv : this.configValueDAO.findBy(template)) {
				result.add(ecv.toApi());
			}
		}
		result.addAll(ReservedConfigKeyStore.instance.getReservedAsConfigValue());
		return result.toArray(new ConfigValue[0]);
	}

	public ConfigValue[] getCleanUnstacked(String template) {
		List<ConfigValue> result = new ArrayList<>();
		for(EConfigValue ecv : this.configValueDAO.findAll(template)) {
			result.add(ecv.toApi());
		}
		return result.toArray(new ConfigValue[0]);
	}

	public ConfigValue[] get(String template, String service) {
		ConfigValue[] result = this.getClean(template, service);
		return this.swapVariables(template, result);
	}

	public ConfigValue[] getClean(String template, String service) {
		Collection<ConfigValue> result = new HashSet<>(Arrays.asList(this.get(template)));
		for(EConfigValue ecv : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_GLOBAL, service)) {
			result.add(ecv.toApi());
		}
		if(!template.equalsIgnoreCase(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			for(EConfigValue ecv : this.configValueDAO.findBy(template, service)) {
				result.add(ecv.toApi());
			}
		}
		result.addAll(ReservedConfigKeyStore.instance.getReservedAsConfigValue());
		return result.toArray(new ConfigValue[0]);
	}

	public String get(String template, String service, String key) {
		String clean = this.getClean(template, service, key);
		return this.swapVariables(template, clean);
	}


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


	private ConfigValue[] swapVariables(String template, ConfigValue[] result) {
		Set<EConfigValue> variables = this.getVariables(template);
		for(ConfigValue configValue : result) {
			for(EConfigValue variable : variables) {
				configValue.setValue(((String) configValue.getValue()).replace(variable.getConfigkey(), variable.getValue()));
			}
		}
		return result;
	}

	private String swapVariables(String template, String result) {
		for(EConfigValue variable : this.getVariables(template)) {
			result = result.replace(variable.getConfigkey(), variable.getValue());
		}
		return result;
	}

	private Set<EConfigValue> getVariables(String template) {
		Set<EConfigValue> variables = new HashSet<>(this.configValueDAO.findBy(template, ConfigValueDAOHib.RESERVED_VARIABLE));
		for(EConfigValue var : this.configValueDAO.findBy(ConfigValueDAOHib.RESERVED_VARIABLE)) {
			if(variables.stream().noneMatch((v) -> v.getConfigkey().equals(var.getConfigkey()))) {
				variables.add(var);
			}
		}
		return variables;
	}
}
