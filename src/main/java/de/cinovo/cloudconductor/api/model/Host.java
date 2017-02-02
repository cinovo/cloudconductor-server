package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import de.cinovo.cloudconductor.api.enums.ServiceState;

import java.util.Map;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class Host {

	private String name;
	private String agent;

	private String description;
	private String template;

	private Long lastSeen;

	private Map<String, ServiceState> services;
	private Map<String, String> packages;


	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the agent
	 */
	public String getAgent() {
		return this.agent;
	}

	/**
	 * @param agent the agent to set
	 */
	public void setAgent(String agent) {
		this.agent = agent;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the lastSeen
	 */
	public Long getLastSeen() {
		return this.lastSeen;
	}

	/**
	 * @param lastSeen the lastSeen to set
	 */
	public void setLastSeen(Long lastSeen) {
		this.lastSeen = lastSeen;
	}

	/**
	 * @return the services
	 */
	public Map<String, ServiceState> getServices() {
		return this.services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(Map<String, ServiceState> services) {
		this.services = services;
	}

	/**
	 * @return the packages
	 */
	public Map<String, String> getPackages() {
		return this.packages;
	}

	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Map<String, String> packages) {
		this.packages = packages;
	}
}
