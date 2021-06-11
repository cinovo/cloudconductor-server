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

import de.cinovo.cloudconductor.api.enums.UpdateRange;
import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "template", schema = "cloudconductor")
public class ETemplate implements IEntity<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String description;
	
	private List<Long> packageVersions;
	
	private List<Long> sshkeys = new ArrayList<>();
	
	private List<Long> repos = new ArrayList<>();
	
	private Boolean autoUpdate;
	private Boolean smoothUpdate;
	private Boolean noUninstalls;
	
	private String group;

	private UpdateRange updateRange;
	
	
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
	 * @return the package versions
	 */
	
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "mappingrpmtemplate", joinColumns = {@JoinColumn(name = "templateid")})
	@Column(name = "rpmid")
	public List<Long> getPackageVersions() {
		return this.packageVersions;
	}
	
	/**
	 * @param rpms the rpms to set
	 */
	public void setPackageVersions(List<Long> rpms) {
		this.packageVersions = rpms;
	}
	
	/**
	 * @return the sshkeys
	 */
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "mappingtemplatesshkey", joinColumns = {@JoinColumn(name = "templateid")})
	@Column(name = "sshkeyid")
	public List<Long> getSshkeys() {
		return this.sshkeys;
	}
	
	/**
	 * @param sshkeys the sshkeys to set
	 */
	public void setSshkeys(List<Long> sshkeys) {
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
	
	/**
	 * @return the noUninstall
	 */
	public Boolean getNoUninstalls() {
		return this.noUninstalls;
	}
	
	/**
	 * @param noUninstalls the noUninstall to set
	 */
	public void setNoUninstalls(Boolean noUninstalls) {
		this.noUninstalls = noUninstalls;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ETemplate)) {
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
	@ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
	@CollectionTable(schema = "cloudconductor", name = "map_template_repo", joinColumns = {@JoinColumn(name = "templateid")})
	@Column(name = "repoid")
	public List<Long> getRepos() {
		return this.repos;
	}
	
	/**
	 * @param repos the repos to set
	 */
	public void setRepos(List<Long> repos) {
		this.repos = repos;
	}
	
	/**
	 * @return the group
	 */
	@Column(name = "groupname")
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * @return the template update range
	 */
	@Column(name = "updaterange")
	public UpdateRange getUpdateRange() {
		return this.updateRange;
	}
	
	/**
	 * @param updateRange the update range to set
	 */
	public void setUpdateRange(UpdateRange updateRange) {
		this.updateRange = updateRange;
	}

	/**
	 * @param hostDAO           the host dao
	 * @param repoDAO           the repo dao
	 * @param packageVersionDAO the package version dao
	 * @return the api object
	 */
	@Transient
	public Template toApi(IHostDAO hostDAO, IRepoDAO repoDAO, IPackageVersionDAO packageVersionDAO) {
		Template template = new Template();
		template.setName(this.name);
		template.setDescription(this.description);
		template.setVersions(packageVersionDAO.findByIds(this.packageVersions).stream().collect(Collectors.toMap(EPackageVersion::getPkgName, EPackageVersion::getVersion, (a, b) -> b)));
		template.setHosts(hostDAO.findHostsForTemplate(this.getId()).stream().map(EHost::toHostIdentifier).collect(Collectors.toSet()));
		template.setRepos(new LinkedHashSet<>(repoDAO.findNamesByIds(this.repos)));
		template.setAutoUpdate(this.autoUpdate);
		template.setSmoothUpdate(this.smoothUpdate);
		template.setNoUninstalls(this.noUninstalls);
		template.setGroup(this.group);
		template.setUpdateRange(this.updateRange);
		return template;
	}
}
