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
	 * @param templateId the template ide
	 * @return the repos
	 */
	List<ERepo> findByTemplate(Long templateId);
}
