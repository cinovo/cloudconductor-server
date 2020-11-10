package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IRepoDAO extends IEntityDAO<ERepo, Long>, IFindNamed<ERepo> {
	/**
	 * @param repoNames repository names
	 * @return list of repositories
	 */
	List<ERepo> findByNames(Iterable<String> repoNames);
	
	/**
	 * @param repos the repo ids
	 * @return the repo names
	 */
	List<String> findNamesByIds(Iterable<Long> repos);
	
	/**
	 * @param repos the repo ids
	 * @return the repos
	 */
	List<ERepo> findByIds(Iterable<Long> repos);
}
