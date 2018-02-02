package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.model.EServerOptions;

import java.util.concurrent.TimeUnit;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IServerTasks extends Runnable {

	/**
	 * @return identifier of this task
	 */
	String getTaskIdentifier();

	/**
	 * @return the execution timer
	 */
	Integer getTimer();


	/**
	 * @return the execution timer unit
	 */
	TimeUnit getTimerUnit();

	/**
	 * @return the initial delay before execution in milliseconds
	 */
	Integer getDelay();

	/**
	 * @param oldSettings the old server settings
	 * @param newSettings the new server settings
	 * @return the neccesary state change
	 */
	TaskStateChange checkStateChange(EServerOptions oldSettings, EServerOptions newSettings);
}
