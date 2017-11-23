package de.cinovo.cloudconductor.server.dao.hibernate;

import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.taimos.dvalin.jpa.EntityDAOHibernate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Repository("JWTTokenDAOHib")
public class JWTTokenDAOHib extends EntityDAOHibernate<EJWTToken, Long>  implements IJWTTokenDAO{

	@Override
	public EJWTToken findByToken(String token) {
		if ((token == null) || token.isEmpty()) {
			return null;
		}
		return this.findByQuery("FROM EJWTToken a WHERE a.token = ?1", token);
	}

	@Override
	public List<EJWTToken> findByRefToken(EUser user, EAuthToken refToken) {
		if ((refToken == null)) {
			return null;
		}
		return this.findListByQuery("FROM EJWTToken a WHERE a.refToken = ?1", refToken);
	}

	@Override
	public Class<EJWTToken> getEntityClass() {
		return EJWTToken.class;
	}
}
