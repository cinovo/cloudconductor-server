package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EService;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@org.springframework.stereotype.Service
public class ServiceHandler {

	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IPackageDAO packageDAO;

	/**
	 * @param s the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public EService createEntity(Service s) throws WebApplicationException {
		EService es = new EService();
		es = this.fillFields(es, s);
		RESTAssert.assertNotNull(es);
		return this.serviceDAO.save(es);
	}

	/**
	 * @param es the entity to update
	 * @param s  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public EService updateEntity(EService es, Service s) throws WebApplicationException {
		es = this.fillFields(es, s);
		RESTAssert.assertNotNull(es);
		return this.serviceDAO.save(es);
	}

	private EService fillFields(EService es, Service s) {
		es.setName(s.getName());
		es.setDescription(s.getDescription());
		es.setInitScript(s.getInitScript());
		if((s.getPackages() != null) && !s.getPackages().isEmpty()) {
			es.setPackages(this.findByName(s.getPackages()));
		} else {
			es.setPackages(null);
		}
		return es;
	}

	private List<EPackage> findByName(Set<String> names) {
		List<EPackage> found = new ArrayList<>();
		for (String s : names) {
			EPackage p = this.packageDAO.findByName(s);
			if (p == null) {
				throw new NotFoundException();
			}
			found.add(p);
		}
		return found;
	}
}
