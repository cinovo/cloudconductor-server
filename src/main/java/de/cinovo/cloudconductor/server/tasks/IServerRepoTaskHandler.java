package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.model.ERepo;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IServerRepoTaskHandler {

	/**
	 * @param repo the repo reference
	 */
	void newRepo(ERepo repo);

	/**
	 * @param repoId the repoId
	 */
	void deleteRepo(long repoId);

	/**
	 * @param repoId the repoId
	 */
	void forceRepoUpdate(long repoId);
}
