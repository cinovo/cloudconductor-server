package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("AgentOptionsDAOHib")
public class AgentOptionsDAOHib extends EntityDAOHibernate<EAgentOption, Long> implements IAgentOptionsDAO {
	
	@Override
	public Class<EAgentOption> getEntityClass() {
		return EAgentOption.class;
	}
	
	@Override
	public EAgentOption findByTemplate(ETemplate template) {
		return this.findByTemplate(template.getId());
	}
	
	@Override
	public EAgentOption findByTemplate(Long templateId) {
		// language=HQL
		return this.findByQuery("FROM EAgentOption AS o WHERE o.templateid = ?1", templateId);
	}
	
}
