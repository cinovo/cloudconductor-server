package de.cinovo.cloudconductor.server.dao;

import java.util.List;

import de.cinovo.cloudconductor.server.model.EFileTag;
import de.taimos.dao.IEntityDAO;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public interface IFileTagsDAO extends IEntityDAO<EFileTag, Long>, IFindNamed<EFileTag> {
	
	/**
	 * @param id the tag ids
	 * @return list of tags
	 */
	public List<EFileTag> findByIds(Long... id);
}
