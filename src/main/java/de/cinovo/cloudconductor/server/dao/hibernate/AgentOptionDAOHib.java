package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dao.hibernate.EntityDAOHibernate;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class AgentOptionDAOHib extends EntityDAOHibernate<EAgentOption, Long> implements IAgentOptionsDAO {
	
	@Override
	public Class<EAgentOption> getEntityClass() {
		return EAgentOption.class;
	}
	
	@Override
	public EAgentOption findByTemplate(String template) {
		return this.findByQuery("FROM EAgentOption ao WHERE ao.template.name = ?1", template);
	}
	
	@Override
	public EAgentOption findByTemplate(ETemplate template) {
		return this.findByQuery("FROM EAgentOption ao WHERE ao.template = ?1", template);
	}
	
}
