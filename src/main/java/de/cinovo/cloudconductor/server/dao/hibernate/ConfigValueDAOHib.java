package de.cinovo.cloudconductor.server.dao.hibernate;

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

import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("ConfigValueDAOHib")
public class ConfigValueDAOHib extends EntityDAOHibernate<EConfigValue, Long> implements IConfigValueDAO {
	
	public static final String RESERVED_GLOBAL = "GLOBAL";
	public static final String RESERVED_VARIABLE = "VARIABLES";
	private static final Set<String> RESERVED = new HashSet<>(Arrays.asList(ConfigValueDAOHib.RESERVED_GLOBAL, ConfigValueDAOHib.RESERVED_VARIABLE));
	
	private static final String BASE_QUERY = "FROM EConfigValue c WHERE c.template = ?1";
	private static final String WHERE_SERVICE = " AND c.service = ?2";
	private static final String WHERE_SERVICE_NULL = " AND (c.service IS NULL OR c.service ='')";
	private static final String WHERE_KEY = " AND c.configkey = ?";

	private static final String TEMPLATES = "SELECT DISTINCT conf.template FROM EConfigValue conf";
	private static final String SERVICES_OF_TEMPLATE = "SELECT DISTINCT conf.service " +
			"FROM EConfigValue conf " +
			"WHERE conf.template = :template";
	@Override
	public Class<EConfigValue> getEntityClass() {
		return EConfigValue.class;
	}
	
	@Override
	public List<EConfigValue> findBy(String template) {
		return this.findList(template, null);
	}
	
	@Override
	public List<EConfigValue> findBy(String template, String service) {
		return this.findList(template, service);
	}
	
	@Override
	public EConfigValue findBy(String template, String service, String key) {
		return this.find(template, service, key);
	}
	
	@Override
	public List<String> findTemplates() {
		List<String> result = this.entityManager.createQuery(ConfigValueDAOHib.TEMPLATES).getResultList();
		if (!result.contains(ConfigValueDAOHib.RESERVED_GLOBAL)) {
			result.add(ConfigValueDAOHib.RESERVED_GLOBAL);
		}
		return result;
	}
	
	@Override
	public Set<String> findRealTemplates() {
		return this.entityManager.createQuery(ConfigValueDAOHib.TEMPLATES, String.class).getResultList().stream().filter(t -> !ConfigValueDAOHib.RESERVED.contains(t)).collect(Collectors.toSet());
	}
	
	@Override
	public Set<String> findServicesForTemplate(String template) {
		Query query = this.entityManager.createQuery(ConfigValueDAOHib.SERVICES_OF_TEMPLATE).setParameter("template", template);
		List<String> result = query.getResultList();
		return result.stream().filter(s -> !ConfigValueDAOHib.RESERVED.contains(s)).collect(Collectors.toSet());
	}
	
	@Override
	public List<EConfigValue> findAll(String template) {
		return this.findListByQuery(ConfigValueDAOHib.BASE_QUERY, template);
	}
	
	@Override
	public List<EConfigValue> findForGlobalTemplate() {
		return this.findBy(ConfigValueDAOHib.RESERVED_GLOBAL);
	}
	
	@Override
	public List<EConfigValue> findForGlobalService(String templateName) {
		return this.findList(templateName, null);
	}
	
	private List<EConfigValue> findList(String template, String service) {
		return this.findListByQuery(this.createQuery(template, service, null), (Object[]) this.getParams(template, service, null));
	}
	
	private EConfigValue find(String template, String service, String key) {
		return this.findByQuery(this.createQuery(template, service, key), (Object[]) this.getParams(template, service, key));
	}
	
	private String createQuery(String template, String service, String key) {
		StringBuilder b = new StringBuilder();
		b.append(ConfigValueDAOHib.BASE_QUERY);
		int app = 2;
		if ((service == null) || service.isEmpty()) {
			b.append(ConfigValueDAOHib.WHERE_SERVICE_NULL);
		} else {
			b.append(ConfigValueDAOHib.WHERE_SERVICE);
			app = 3;
		}
		if ((key != null) && !(key.isEmpty())) {
			b.append(ConfigValueDAOHib.WHERE_KEY);
			b.append(app);
		}
		
		return b.toString();
	}
	
	private String[] getParams(String... params) {
        return Arrays.stream(params).filter(p -> p != null && !p.isEmpty()).toArray(String[]::new);
	}
    
}
