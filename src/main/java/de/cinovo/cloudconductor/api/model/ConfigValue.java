package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.Objects;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class ConfigValue implements Comparable<ConfigValue> {

    private String key;
    private Object value;

    private String template;
    private String service;

    /**
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        if(service == null || service.isEmpty()) {
            this.service = null;
        }else {
            this.service = service;
        }
    }

    @Override
    public int compareTo(ConfigValue o) {
        return this.key.compareTo(o.getKey());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConfigValue && Objects.equals(((ConfigValue) o).key, this.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

}
