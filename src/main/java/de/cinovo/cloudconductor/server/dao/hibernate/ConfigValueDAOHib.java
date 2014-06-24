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

import java.util.List;

import org.springframework.stereotype.Repository;

import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.model.EConfigValue;
import de.cinovo.cloudconductor.server.model.enums.AuditCategory;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Repository("ConfigValueDAOHib")
public class ConfigValueDAOHib extends AVersionedEntityHib<EConfigValue> implements IConfigValueDAO {
	
	@Override
	public Class<EConfigValue> getEntityClass() {
		return EConfigValue.class;
	}
	
	@Override
	public List<EConfigValue> findGlobal() {
		return this.findVersionedListByQuery("FROM EConfigValue c WHERE c.template = 'GLOBAL' AND (c.service IS NULL OR c.service ='') ", "c");
	}
	
	@Override
	public List<EConfigValue> findGlobal(String service) {
		return this.findVersionedListByQuery("FROM EConfigValue c WHERE c.template = 'GLOBAL' AND c.service = ?1", "c", service);
	}
	
	@Override
	public EConfigValue findGlobal(String service, String key) {
		return this.findVersionedByQuery("FROM EConfigValue c WHERE c.template = 'GLOBAL' AND c.service = ?1 AND c.configkey = ?2", "c", service, key);
	}
	
	@Override
	public List<EConfigValue> findBy(String template) {
		return this.findVersionedListByQuery("FROM EConfigValue c WHERE c.template = ?1  AND (c.service IS NULL OR c.service ='')", "c", template);
	}
	
	@Override
	public List<EConfigValue> findBy(String template, String service) {
		return this.findVersionedListByQuery("FROM EConfigValue c WHERE c.template = ?1 AND c.service = ?2", "c", template, service);
	}
	
	@Override
	public EConfigValue findBy(String template, String service, String key) {
		return this.findVersionedByQuery("FROM EConfigValue c WHERE c.template = ?1 AND c.service = ?2 AND c.configkey = ?3", "c", template, service, key);
	}
	
	@Override
	public EConfigValue findKey(String key) {
		return this.findVersionedByQuery("FROM EConfigValue c WHERE c.template = 'GLOBAL' AND (c.service IS NULL OR c.service ='') AND c.configkey = ?1", "c", key);
	}
	
	@Override
	public EConfigValue findKey(String template, String key) {
		return this.findVersionedByQuery("FROM EConfigValue c WHERE c.template = ?1 AND (c.service IS NULL OR c.service ='') AND c.configkey = ?2", "c", template, key);
	}
	
	@Override
	public List<EConfigValue> findAll(String template) {
		return this.findVersionedListByQuery("FROM EConfigValue c WHERE c.template = ?1", "c", template);
	}
	
	@Override
	public List<String> findTemplates() {
		return this.entityManager.createQuery("SELECT DISTINCT conf.template FROM EConfigValue conf").getResultList();
	}
	
	@Override
	public AuditCategory getAuditCategory() {
		return AuditCategory.CONFIG;
	}
	
}
