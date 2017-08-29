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

import java.util.List;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.cinovo.cloudconductor.api.interfaces.INamed;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity
@Table(name = "file", schema = "cloudconductor")
public class EFile extends AModelApiConvertable<ConfigFile> implements IVersionized<Long>, INamed {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private EPackage pkg;
	private String targetPath;
	private String owner;
	private String group;
	private String fileMode;
	private boolean isTemplate;
	private boolean isReloadable;
	private String checksum;
	private List<EService> dependentServices;
	private List<ETemplate> templates;
	
	private Long version;
	private boolean deleted = false;
	private Long origId;
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the pkg
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "packageid")
	public EPackage getPkg() {
		return this.pkg;
	}
	
	/**
	 * @param pkg the pkg to set
	 */
	public void setPkg(EPackage pkg) {
		this.pkg = pkg;
	}
	
	/**
	 * @return the targetPath
	 */
	public String getTargetPath() {
		return this.targetPath;
	}
	
	/**
	 * @param targetPath the targetPath to set
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return this.owner;
	}
	
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the group
	 */
	@Column(name = "filegroup", nullable = false)
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
	 * @return the fileMode
	 */
	public String getFileMode() {
		return this.fileMode;
	}
	
	/**
	 * @param fileMode the fileMode to set
	 */
	public void setFileMode(String fileMode) {
		this.fileMode = fileMode;
	}
	
	/**
	 * @return the isTemplate
	 */
	public boolean isTemplate() {
		return this.isTemplate;
	}
	
	/**
	 * @param isTemplate the isTemplate to set
	 */
	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}
	
	/**
	 * @return the isReloadable
	 */
	public boolean isReloadable() {
		return this.isReloadable;
	}
	
	/**
	 * @param isReloadable the isReloadable to set
	 */
	public void setReloadable(boolean isReloadable) {
		this.isReloadable = isReloadable;
	}
	
	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return this.checksum;
	}
	
	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	/**
	 * @return the dependentServices
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingfileservice", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "fileid"), inverseJoinColumns = @JoinColumn(name = "serviceid"))
	public List<EService> getDependentServices() {
		return this.dependentServices;
	}
	
	/**
	 * @param dependentServices the dependentServices to set
	 */
	public void setDependentServices(List<EService> dependentServices) {
		this.dependentServices = dependentServices;
	}
	
	/**
	 * @return list of templates this file is used in
	 */
	@ManyToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
	@JoinTable(name = "mappingfiletemplate", schema = "cloudconductor", //
	joinColumns = @JoinColumn(name = "fileid"), inverseJoinColumns = @JoinColumn(name = "templateid"))
	public List<ETemplate> getTemplates() {
		return this.templates;
	}
	
	/**
	 * @param templates the list of templates to set
	 */
	public void setTemplates(List<ETemplate> templates) {
		this.templates = templates;
	}
	
	/**
	 * @return the name
	 */
	@Override
	@Column(nullable = false)
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isDeleted() {
		return this.deleted;
	}
	
	/**
	 * @param deleted the deleted to set
	 */
	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public void setOrigId(Long id) {
		this.origId = id;
	}
	
	@Override
	public Long getOrigId() {
		return this.origId;
	}
	
	@Override
	public Long getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	@Override
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EFile)) {
			return false;
		}
		EFile other = (EFile) obj;
		return (this.getName() != null) && this.getName().equals(other.getName());
	}
	
	@Override
	public int hashCode() {
		return (this.getName() == null) ? 0 : this.getName().hashCode();
	}
	
	@Override
	public IEntity<Long> cloneNew() {
		EFile r = new EFile();
		r.setChecksum(this.checksum);
		r.setDeleted(this.deleted);
		r.setDependentServices(this.dependentServices);
		r.setFileMode(this.fileMode);
		r.setGroup(this.group);
		r.setName(this.name);
		r.setOrigId(this.origId);
		r.setOwner(this.owner);
		r.setPkg(this.pkg);
		r.setReloadable(this.isReloadable);
		r.setTargetPath(this.targetPath);
		r.setTemplate(this.isTemplate);
		r.setVersion(this.version);
		return r;
	}
	
	@Override
	@Transient
	public Class<ConfigFile> getApiClass() {
		return ConfigFile.class;
	}
	
	@Override
	@Transient
	public ConfigFile toApi() {
		ConfigFile configFile = super.toApi();
		configFile.setDependentServices(this.namedModelToStringSet(this.dependentServices));
		configFile.setTemplates(this.namedModelToStringSet(this.templates));
		return configFile;
	}
}
