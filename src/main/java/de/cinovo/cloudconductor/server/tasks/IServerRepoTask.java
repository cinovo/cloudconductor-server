package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IServerRepoTask {

	/**
	 * @param settings the server settings to be passed to the task
	 */
	void create(EServerOptions settings);

	/**
	 * @param repo the repo reference
	 */
	void newRepo(ERepo repo);

	/**
	 * @param oldRepoName the old repo name
	 * @param newRepo     the new repo reference
	 */
	void updateRepo(String oldRepoName, ERepo newRepo);

	/**
	 * @param repoName the repo name
	 */
	void deleteRepo(String repoName);


	/**
	 * @param oldSettings the old server settings
	 * @param newSettings the new server settings
	 */
	void update(EServerOptions oldSettings, EServerOptions newSettings);
}
