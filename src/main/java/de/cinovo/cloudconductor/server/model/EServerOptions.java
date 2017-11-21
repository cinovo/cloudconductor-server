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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.cinovo.cloudconductor.api.model.Settings;
import de.taimos.dvalin.jpa.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
@Entity
@Table(name = "serveroptions", schema = "cloudconductor")
public class EServerOptions extends AModelApiConvertable<Settings> implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String name;
	
	private boolean allowautoupdate;
	
	private String description;
	
	private boolean needsApproval;
	
	private int hostAliveTimer = 15;
	private TimeUnit hostAliveTimerUnit = TimeUnit.MINUTES;
	
	private int hostCleanUpTimer = 30;
	private TimeUnit hostCleanUpTimerUnit = TimeUnit.MINUTES;
	
	private int indexScanTimer = 60;
	private TimeUnit indexScanTimerUnit = TimeUnit.SECONDS;
	
	private int pageRefreshTimer = 15;
	private TimeUnit pageRefreshTimerUnit = TimeUnit.SECONDS;
	
	private Set<String> disallowUninstall = new HashSet<>();
	
	
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
	 * @return the allowautoupdate
	 */
	public boolean isAllowautoupdate() {
		return this.allowautoupdate;
	}
	
	/**
	 * @param allowautoupdate the allowautoupdate to set
	 */
	public void setAllowautoupdate(boolean allowautoupdate) {
		this.allowautoupdate = allowautoupdate;
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
	 * @return the needsApproval
	 */
	public boolean isNeedsApproval() {
		return this.needsApproval;
	}
	
	/**
	 * @param needsApproval the needsApproval to set
	 */
	public void setNeedsApproval(boolean needsApproval) {
		this.needsApproval = needsApproval;
	}
	
	/**
	 * @return the hostAliveTimer
	 */
	public int getHostAliveTimer() {
		return this.hostAliveTimer;
	}
	
	/**
	 * @param hostAliveTimer the hostAliveTimer to set
	 */
	public void setHostAliveTimer(int hostAliveTimer) {
		this.hostAliveTimer = hostAliveTimer;
	}
	
	/**
	 * @return the hostAliveTimerUnit
	 */
	public TimeUnit getHostAliveTimerUnit() {
		return this.hostAliveTimerUnit;
	}
	
	/**
	 * @param hostAliveTimerUnit the hostAliveTimerUnit to set
	 */
	public void setHostAliveTimerUnit(TimeUnit hostAliveTimerUnit) {
		this.hostAliveTimerUnit = hostAliveTimerUnit;
	}
	
	/**
	 * @return the hostCleanUpTimer
	 */
	public int getHostCleanUpTimer() {
		return this.hostCleanUpTimer;
	}
	
	/**
	 * @param hostCleanUpTimer the hostCleanUpTimer to set
	 */
	public void setHostCleanUpTimer(int hostCleanUpTimer) {
		this.hostCleanUpTimer = hostCleanUpTimer;
	}
	
	/**
	 * @return the hostCleanUpTimerUnit
	 */
	public TimeUnit getHostCleanUpTimerUnit() {
		return this.hostCleanUpTimerUnit;
	}
	
	/**
	 * @param hostCleanUpTimerUnit the hostCleanUpTimerUnit to set
	 */
	public void setHostCleanUpTimerUnit(TimeUnit hostCleanUpTimerUnit) {
		this.hostCleanUpTimerUnit = hostCleanUpTimerUnit;
	}
	
	/**
	 * @return the indexScanTimer
	 */
	public int getIndexScanTimer() {
		return this.indexScanTimer;
	}
	
	/**
	 * @param indexScanTimer the indexScanTimer to set
	 */
	public void setIndexScanTimer(int indexScanTimer) {
		this.indexScanTimer = indexScanTimer;
	}
	
	/**
	 * @return the indexScanTimerUnit
	 */
	public TimeUnit getIndexScanTimerUnit() {
		return this.indexScanTimerUnit;
	}
	
	/**
	 * @param indexScanTimerUnit the indexScanTimerUnit to set
	 */
	public void setIndexScanTimerUnit(TimeUnit indexScanTimerUnit) {
		this.indexScanTimerUnit = indexScanTimerUnit;
	}
	
	/**
	 * @return the pageRefreshTimer
	 */
	public int getPageRefreshTimer() {
		return this.pageRefreshTimer;
	}
	
	/**
	 * @param pageRefreshTimer the pageRefreshTimer to set
	 */
	public void setPageRefreshTimer(int pageRefreshTimer) {
		this.pageRefreshTimer = pageRefreshTimer;
	}
	
	/**
	 * @return the pageRefreshTimerUnit
	 */
	public TimeUnit getPageRefreshTimerUnit() {
		return this.pageRefreshTimerUnit;
	}
	
	/**
	 * @param pageRefreshTimerUnit the pageRefreshTimerUnit to set
	 */
	public void setPageRefreshTimerUnit(TimeUnit pageRefreshTimerUnit) {
		this.pageRefreshTimerUnit = pageRefreshTimerUnit;
	}
	
	/**
	 * @return the disallowUninstall
	 */
	@ElementCollection
	@CollectionTable(name = "serveroptions_disallowuninstall", joinColumns = @JoinColumn(name = "id"), schema = "cloudconductor")
	@Column(name = "disallowuninstall")
	public Set<String> getDisallowUninstall() {
		return this.disallowUninstall;
	}
	
	/**
	 * @param disallowUninstall the disallowUninstall to set
	 */
	public void setDisallowUninstall(Set<String> disallowUninstall) {
		this.disallowUninstall = disallowUninstall;
	}
	
	@Override
	@Transient
	public Class<Settings> getApiClass() {
		return Settings.class;
	}
}
