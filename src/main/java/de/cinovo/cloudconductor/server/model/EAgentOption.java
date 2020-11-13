package de.cinovo.cloudconductor.server.model;

import de.cinovo.cloudconductor.api.enums.TaskState;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.taimos.dvalin.jpa.IEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Entity
@Table(name = "agentoption", schema = "cloudconductor")
public class EAgentOption implements IEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long templateid;
	
	private int aliveTimer = 1;
	private TimeUnit aliveTimerUnit = TimeUnit.MINUTES;
	
	private TaskState doSshKeys = TaskState.REPEAT;
	private int sshKeysTimer = 5;
	private TimeUnit sshKeysTimerUnit = TimeUnit.MINUTES;
	
	private TaskState doPackageManagement = TaskState.REPEAT;
	private int packageManagementTimer = 2;
	private TimeUnit packageManagementTimerUnit = TimeUnit.MINUTES;
	
	private TaskState doFileManagement = TaskState.REPEAT;
	private int fileManagementTimer = 2;
	private TimeUnit fileManagementTimerUnit = TimeUnit.MINUTES;
	
	
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
	 * @return the template
	 */
	public Long getTemplateid() {
		return this.templateid;
	}
	
	/**
	 * @param template the template to set
	 */
	public void setTemplateid(Long template) {
		this.templateid = template;
	}
	
	/**
	 * @return the aliveTimer
	 */
	public int getAliveTimer() {
		return this.aliveTimer;
	}
	
	/**
	 * @param aliveTimer the aliveTimer to set
	 */
	public void setAliveTimer(int aliveTimer) {
		this.aliveTimer = aliveTimer;
	}
	
	/**
	 * @return the aliveTimerUnit
	 */
	public TimeUnit getAliveTimerUnit() {
		return this.aliveTimerUnit;
	}
	
	/**
	 * @param aliveTimerUnit the aliveTimerUnit to set
	 */
	public void setAliveTimerUnit(TimeUnit aliveTimerUnit) {
		this.aliveTimerUnit = aliveTimerUnit;
	}
	
	/**
	 * @return the doSshKeys
	 */
	public TaskState getDoSshKeys() {
		return this.doSshKeys;
	}
	
	/**
	 * @param doSshKeys the doSshKeys to set
	 */
	public void setDoSshKeys(TaskState doSshKeys) {
		this.doSshKeys = doSshKeys;
	}
	
	/**
	 * @return the sshKeysTimer
	 */
	public int getSshKeysTimer() {
		return this.sshKeysTimer;
	}
	
	/**
	 * @param sshKeysTimer the sshKeysTimer to set
	 */
	public void setSshKeysTimer(int sshKeysTimer) {
		this.sshKeysTimer = sshKeysTimer;
	}
	
	/**
	 * @return the sshKeysTimerUnit
	 */
	public TimeUnit getSshKeysTimerUnit() {
		return this.sshKeysTimerUnit;
	}
	
	/**
	 * @param sshKeysTimerUnit the sshKeysTimerUnit to set
	 */
	public void setSshKeysTimerUnit(TimeUnit sshKeysTimerUnit) {
		this.sshKeysTimerUnit = sshKeysTimerUnit;
	}
	
	/**
	 * @return the doPackageManagement
	 */
	public TaskState getDoPackageManagement() {
		return this.doPackageManagement;
	}
	
	/**
	 * @param doPackageManagement the doPackageManagement to set
	 */
	public void setDoPackageManagement(TaskState doPackageManagement) {
		this.doPackageManagement = doPackageManagement;
	}
	
	/**
	 * @return the packageManagementTimer
	 */
	public int getPackageManagementTimer() {
		return this.packageManagementTimer;
	}
	
	/**
	 * @param packageManagementTimer the packageManagementTimer to set
	 */
	public void setPackageManagementTimer(int packageManagementTimer) {
		this.packageManagementTimer = packageManagementTimer;
	}
	
	/**
	 * @return the packageManagementTimerUnit
	 */
	public TimeUnit getPackageManagementTimerUnit() {
		return this.packageManagementTimerUnit;
	}
	
	/**
	 * @param packageManagementTimerUnit the packageManagementTimerUnit to set
	 */
	public void setPackageManagementTimerUnit(TimeUnit packageManagementTimerUnit) {
		this.packageManagementTimerUnit = packageManagementTimerUnit;
	}
	
	/**
	 * @return the doFileManagement
	 */
	public TaskState getDoFileManagement() {
		return this.doFileManagement;
	}
	
	/**
	 * @param doFileManagement the doFileManagement to set
	 */
	public void setDoFileManagement(TaskState doFileManagement) {
		this.doFileManagement = doFileManagement;
	}
	
	/**
	 * @return the fileManagementTimer
	 */
	public int getFileManagementTimer() {
		return this.fileManagementTimer;
	}
	
	/**
	 * @param fileManagementTimer the fileManagementTimer to set
	 */
	public void setFileManagementTimer(int fileManagementTimer) {
		this.fileManagementTimer = fileManagementTimer;
	}
	
	/**
	 * @return the fileManagementTimerUnit
	 */
	public TimeUnit getFileManagementTimerUnit() {
		return this.fileManagementTimerUnit;
	}
	
	/**
	 * @param fileManagementTimerUnit the fileManagementTimerUnit to set
	 */
	public void setFileManagementTimerUnit(TimeUnit fileManagementTimerUnit) {
		this.fileManagementTimerUnit = fileManagementTimerUnit;
	}
	
	/**
	 * @return api classe
	 */
	@Transient
	public Class<AgentOption> getApiClass() {
		return AgentOption.class;
	}
	
	/**
	 * @param templateDAO the ITemplateDAO
	 * @return the api object
	 */
	@Transient
	public AgentOption toApi(ITemplateDAO templateDAO) {
		String templateName = "";
		if (templateDAO != null) {
			ETemplate template = templateDAO.findById(this.templateid);
			templateName = template.getName();
		}
		return new AgentOption(this.aliveTimer, this.aliveTimerUnit, //
				this.doSshKeys, this.sshKeysTimer, this.sshKeysTimerUnit, //
				this.doPackageManagement, this.packageManagementTimer, this.packageManagementTimerUnit, //
				this.doFileManagement, this.fileManagementTimer, this.fileManagementTimerUnit, //
				templateName);
	}
	
	/**
	 * @param template the template
	 * @return the api object
	 */
	@Transient
	public AgentOption toApi(ETemplate template) {
		String templateName = "";
		if (template != null) {
			templateName = template.getName();
		}
		return new AgentOption(this.aliveTimer, this.aliveTimerUnit, //
				this.doSshKeys, this.sshKeysTimer, this.sshKeysTimerUnit, //
				this.doPackageManagement, this.packageManagementTimer, this.packageManagementTimerUnit, //
				this.doFileManagement, this.fileManagementTimer, this.fileManagementTimerUnit, //
				templateName);
	}
}
