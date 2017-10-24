package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.model.EServerOptions;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public interface IServerTasks extends Runnable {
	
	/**
	 * @return identifier of this task
	 */
	String getTaskIdentifier();
	
	/**
	 * @param settings the server settings to be passed to the task
	 */
	void create(EServerOptions settings);
	
	/**
	 * @param oldSettings the old server settings
	 * @param newSettings the new server settings
	 */
	void update(EServerOptions oldSettings, EServerOptions newSettings);
}
