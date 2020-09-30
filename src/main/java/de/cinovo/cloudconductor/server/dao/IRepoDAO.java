package de.cinovo.cloudconductor.server.dao;

import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.dvalin.jpa.IEntityDAO;

import java.util.List;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public interface IRepoDAO extends IEntityDAO<ERepo, Long>, IFindNamed<ERepo> {
	/**
	 * @param templateId the template id
	 * @return the repos
	 */
	List<ERepo> findByTemplate(Long templateId);

	List<String> findNamesByTemplate(String templateName);

	/**
	 * @param templateName	the name of the template
	 * @return list of repos for template
	 */
	List<ERepo> findByTemplate(String templateName);

	/**
	 * @param repoNames	repository names
	 * @return list of repositories
	 */
	List<ERepo> findByNames(Iterable<String> repoNames);
}
