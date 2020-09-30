package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.api.model.AgentOption;
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
 *
 */
@Repository("AgentOptionsDAOHib")
public class AgentOptionsDAOHib extends EntityDAOHibernate<EAgentOption, Long> implements IAgentOptionsDAO {

	@Override
	public Class<EAgentOption> getEntityClass() {
		return EAgentOption.class;
	}

	@Override
	public AgentOption findFlatByTemplate(String templateName) {
		// language=HQL
		String q = "SELECT NEW de.cinovo.cloudconductor.api.model.AgentOption(" + //
				" o.aliveTimer, o.aliveTimerUnit," + //
				" o.doSshKeys, o.sshKeysTimer, o.sshKeysTimerUnit," + //
				" o.doPackageManagement, o.packageManagementTimer, o.packageManagementTimerUnit," + //
				" o.doFileManagement, o.fileManagementTimer, o.fileManagementTimerUnit," + //
				" o.template.name)" + //
				" FROM EAgentOption AS o" +
				" WHERE o.template.name = ?1";
		return this.entityManager.createQuery(q, AgentOption.class).setParameter(1, templateName).getResultList().stream().findFirst().orElse (null);
	}

	@Override
	public EAgentOption findByTemplate(String templateName) {
		// language=HQL
		return this.findByQuery("FROM EAgentOption AS o WHERE o.template.name = ?1", templateName);
	}

	@Override
	public EAgentOption findByTemplate(ETemplate template) {
		// language=HQL
		return this.findByQuery("FROM EAgentOption AS o WHERE o.template = ?1", template);
	}

}
