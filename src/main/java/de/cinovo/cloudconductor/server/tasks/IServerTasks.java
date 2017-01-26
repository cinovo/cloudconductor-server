package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.model.EServerOptions;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public interface IServerTasks extends Runnable{

	String getTaskIdentifier();

	void create(EServerOptions settings);

	void update(EServerOptions oldSettings, EServerOptions newSettings);
}
