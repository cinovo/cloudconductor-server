package de.cinovo.cloudconductor.server.util.comparators;

import de.cinovo.cloudconductor.api.model.ConfigDiff;
import de.cinovo.cloudconductor.api.model.ConfigValue;
import de.cinovo.cloudconductor.server.handler.ConfigValueHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class ConfigValueDiffer {

	@Autowired
	private ConfigValueHandler handler;

	/**
	 * @param templateNameA template a
	 * @param templateNameB template b
	 * @return array of pacakges difference between the given templates for the biggest installed version of the package
	 */
	public ConfigDiff[] compare(String templateNameA, String templateNameB) {
		ConfigValue[] tA = this.handler.getCleanUnstacked(templateNameA);
		ConfigValue[] tB = this.handler.getCleanUnstacked(templateNameB);
		Set<ConfigDiff> res = new HashSet<>();
		Set<ConfigValue> notFound = new HashSet<>(Arrays.asList(tA));
		for(ConfigValue cvB : tB) {
			boolean found = false;

			for(ConfigValue cvA : tA) {

				if(!Objects.equals(cvA.getService(), cvB.getService())) {
					continue;
				}
				if(!cvA.getKey().equals(cvB.getKey())) {
					continue;
				}
				notFound.remove(cvA);
				found = true;
				if(!cvA.getValue().equals(cvB.getValue())) {
					res.add(this.toApi(cvA, cvB));
					break;
				}
			}
			if(!found) {
				res.add(this.toApi(null, cvB));
			}
		}
		for(ConfigValue cvA : notFound) {
			res.add(this.toApi(cvA, null));
		}

		return res.toArray(new ConfigDiff[0]);
	}

	private ConfigDiff toApi(ConfigValue cvA, ConfigValue cvB) {
		ConfigDiff diff = new ConfigDiff();
		diff.setKey(cvA != null ? cvA.getKey() : cvB.getKey());
		diff.setService(cvA != null ? cvA.getService() : cvB.getService());
		if(cvA != null) {
			diff.setValueA((String) cvA.getValue());
		}
		if(cvB != null) {
			diff.setValueB((String) cvB.getValue());
		}
		return diff;
	}
}
