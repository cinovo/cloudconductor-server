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
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("ConfigValueDAOHib")
public class ConfigValueDAOHib extends AVersionedEntityHib<EConfigValue> implements IConfigValueDAO {

    public static final String RESERVED_GLOBAL = "GLOBAL";

    private static final String BASE_QUERY = "FROM EConfigValue c WHERE c.template = ?1";
    private static final String WHERE_SERVICE = " AND c.service = ?2";
    private static final String WHERE_SERVICE_NULL = " AND (c.service IS NULL OR c.service ='')";
    private static final String WHERE_KEY = " AND c.configkey = ?";

    @SuppressWarnings("JpaQlInspection")
    private static final String TEMPLATES = "SELECT DISTINCT conf.template FROM EConfigValue conf WHERE conf.deleted = false";

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
        if(!result.contains(ConfigValueDAOHib.RESERVED_GLOBAL)) {
            result.add(ConfigValueDAOHib.RESERVED_GLOBAL);
        }
        return result;
    }

    @Override
    public List<EConfigValue> findAll(String template) {
        return this.findVersionedListByQuery(ConfigValueDAOHib.BASE_QUERY, "c", template);
    }

    private List<EConfigValue> findList(String template, String service) {
        return this.findVersionedListByQuery(this.createQuery(template, service, null), "c", this.getParams(template, service, null));
    }


    private EConfigValue find(String template, String service, String key) {
        return this.findVersionedByQuery(this.createQuery(template, service, key), "c", this.getParams(template, service, key));
    }


    private String createQuery(String template, String service, String key) {
        StringBuilder b = new StringBuilder();
        b.append(BASE_QUERY);
        int app = 2;
        if(service == null || service.isEmpty()) {
            b.append(WHERE_SERVICE_NULL);
        } else {
            b.append(WHERE_SERVICE);
            app = 3;
        }
        if(key != null && !(key.isEmpty())) {
            b.append(WHERE_KEY);
            b.append(app);
        }

        return b.toString();
    }

    private String[] getParams(String... params) {
        List<String> req = new ArrayList<>();
        for(String s : params) {
            if(s != null && !(s.isEmpty())) {
                req.add(s);
            }
        }
        return req.toArray(new String[req.size()]);
    }
}
