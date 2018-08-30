package de.cinovo.cloudconductor.server.model;

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

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.SimpleTemplate;
import de.cinovo.cloudconductor.api.model.Template;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "template", schema = "cloudconductor")
public class ETemplate extends AModelApiConvertable<Template> implements IEntity<Long>, INamed {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;

	private List<EPackageVersion> packageVersions;

	private List<EHost> hosts = new ArrayList<>();

	private List<ESSHKey> sshkeys = new ArrayList<>();

	private List<EFile> configFiles = new ArrayList<>();

	private List<ERepo> repos = new ArrayList<>();

	private Boolean autoUpdate;

	private Boolean smoothUpdate;

	private String group;


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@Override
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
	 * @return the configFiles
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingfiletemplate", schema = "cloudconductor", //
			joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "fileid"))
	public List<EFile> getConfigFiles() {
		return this.configFiles;
	}

	/**
	 * @param configFiles the configFiles to set
	 */
	public void setConfigFiles(List<EFile> configFiles) {
		this.configFiles = configFiles;
	}

	/**
	 * @return the hosts
	 */
	@OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
	public List<EHost> getHosts() {
		return this.hosts;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(List<EHost> hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the rpms
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JoinTable(name = "mappingrpmtemplate", schema = "cloudconductor", //
			joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "rpmid"))
	public List<EPackageVersion> getPackageVersions() {
		return this.packageVersions;
	}

	/**
	 * @param rpms the rpms to set
	 */
	public void setPackageVersions(List<EPackageVersion> rpms) {
		this.packageVersions = rpms;
	}

	/**
	 * @return the sshkeys
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingtemplatesshkey", schema = "cloudconductor", //
			joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "sshkeyid"))
	public List<ESSHKey> getSshkeys() {
		return this.sshkeys;
	}

	/**
	 * @param sshkeys the sshkeys to set
	 */
	public void setSshkeys(List<ESSHKey> sshkeys) {
		this.sshkeys = sshkeys;
	}

	/**
	 * @return the autoUpdate
	 */
	public Boolean getAutoUpdate() {
		return this.autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(Boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the smoothUpdate
	 */
	public Boolean getSmoothUpdate() {
		return this.smoothUpdate;
	}

	/**
	 * @param smoothUpdate the smoothUpdate to set
	 */
	public void setSmoothUpdate(Boolean smoothUpdate) {
		this.smoothUpdate = smoothUpdate;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ETemplate)) {
			return false;
		}
		ETemplate other = (ETemplate) obj;
		return (this.getName() != null) && this.getName().equals(other.getName());
	}

	@Override
	public int hashCode() {
		return (this.getName() == null) ? 0 : this.getName().hashCode();
	}

	/**
	 * @return the repos
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JoinTable(name = "map_template_repo", schema = "cloudconductor", //
			joinColumns = @JoinColumn(name = "templateid"), inverseJoinColumns = @JoinColumn(name = "repoid"))
	public List<ERepo> getRepos() {
		return this.repos;
	}

	/**
	 * @param repos the repos to set
	 */
	public void setRepos(List<ERepo> repos) {
		this.repos = repos;
	}

	/**
	 * @return the group
	 */
	@Column(name = "\"group\"")
	public String getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	@Transient
	public Class<Template> getApiClass() {
		return Template.class;
	}

	@Override
	public Template toApi() {
		Template template = super.toApi();
		if(!this.hosts.isEmpty()) {
			template.setHosts(this.hosts.stream().map(EHost::toHostIdentifier).collect(Collectors.toSet()));
		} else {
			template.setHosts(new HashSet<>());
		}
		template.setRepos(this.namedModelToStringSet(this.repos));

		Map<String, String> versions = new HashMap<>();
		for(EPackageVersion pv : this.packageVersions) {
			versions.put(pv.getPkg().getName(), pv.getVersion());
		}
		template.setVersions(versions);
		return template;
	}

	/**
	 * @return simple api element
	 */
	public SimpleTemplate toSimple() {
		SimpleTemplate res = new SimpleTemplate();
		res.setGroup(this.group);
		res.setHostCount(0);
		if(!this.hosts.isEmpty()) {
			res.setHostCount(this.hosts.size());
		}
		res.setName(this.name);
		res.setPackageCount(0);
		if(this.packageVersions != null && !this.packageVersions.isEmpty()) {
			res.setPackageCount(this.packageVersions.size());
		}
		res.setRepos(this.repos.stream().map(ERepo::getName).collect(Collectors.toSet()));
		return res;
	}
}
