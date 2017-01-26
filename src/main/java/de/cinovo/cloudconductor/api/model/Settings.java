package de.cinovo.cloudconductor.api.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.CLASS)
public class Settings {

	private String name;
	private String description;

	private boolean allowautoupdate;
	private boolean needsApproval;

	private int hostCleanUpTimer = 30;
	private TimeUnit hostCleanUpTimerUnit = TimeUnit.MINUTES;

	private int indexScanTimer = 60;
	private TimeUnit indexScanTimerUnit = TimeUnit.SECONDS;

	private int pageRefreshTimer = 15;
	private TimeUnit pageRefreshTimerUnit = TimeUnit.SECONDS;

	private Set<String> disallowUninstall = new HashSet<>();

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
	public Set<String> getDisallowUninstall() {
		return this.disallowUninstall;
	}

	/**
	 * @param disallowUninstall the disallowUninstall to set
	 */
	public void setDisallowUninstall(Set<String> disallowUninstall) {
		this.disallowUninstall = disallowUninstall;
	}
}
