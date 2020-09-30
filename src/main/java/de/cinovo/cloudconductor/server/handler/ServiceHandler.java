package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import de.taimos.restutils.RESTAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@org.springframework.stereotype.Service
public class ServiceHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHandler.class);
	
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IServiceDefaultStateDAO serviceDefaultStateDAO;

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
	 * @param s the update data
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
		if ((s.getPackages() != null) && !s.getPackages().isEmpty()) {
			es.setPackages(this.packageDAO.findByName(s.getPackages()));
		} else {
			es.setPackages(null);
		}
		return es;
	}
	
	/**
	 * @param template the template
	 * @param host the host
	 * @return true if there are services missing, false otherwise
	 */
	public boolean assertHostServices(ETemplate template, EHost host) {
		// first find out which services are needed
		Set<EService> templateServices = new HashSet<>(this.serviceDAO.findByTemplate(template.getName()));
		ServiceHandler.LOGGER.debug("Found " + templateServices.size() + " services for template '" + template.getName() + "' on  host '" + host.getName() + "'");
		
		Set<EService> missingServices = new HashSet<>(templateServices);
		Set<EServiceState> nonUsedServiceStates = new HashSet<>(host.getServices());
		for (EServiceState state : host.getServices()) {
			for (EService service : templateServices) {
				if (service.getName().equals(state.getService().getName())) {
					missingServices.remove(service);
					for (EServiceState ss : nonUsedServiceStates) {
						if (ss.getService().getId().equals(service.getId())) {
							nonUsedServiceStates.remove(ss);
							break;
						}
					}
					break;
				}
			}
		}
		
		ServiceHandler.LOGGER.debug(missingServices.size() + " services missing and " + nonUsedServiceStates.size() + " unused.");
		
		boolean changes = false;
		// add new service states
		for (EService service : missingServices) {
			EServiceState state = new EServiceState();
			state.setService(service);
			state.setHost(host);
			
			EServiceDefaultState dss = this.serviceDefaultStateDAO.findByName(service.getName(), template.getName());
			if ((dss != null)) {
				state.setState(dss.getState());
			}
			
			this.serviceStateDAO.save(state);
			changes = true;
		}
		
		// clean up old no more used service states
		for (EServiceState ss : nonUsedServiceStates) {
			this.serviceStateDAO.delete(ss);
		}
		return changes;
	}
	
	/**
	 * @param serviceName the name of the service
	 * @return service usage map
	 */
	@Transactional
	public Map<String, String> getServiceUsage(String serviceName) {
		RESTAssert.assertNotEmpty(serviceName);
		RESTAssert.assertTrue(this.serviceDAO.exists(serviceName), Response.Status.NOT_FOUND);
		return packageDAO.findServiceUsage(serviceName);
	}
	
	/**
	 * @return map of all service usages
	 */
	@Transactional
	public Map<String, Map<String, String>> getServiceUsage() {
		return this.serviceDAO.findList().stream()//
		.collect(Collectors.toMap(EService::getName, s -> this.getServiceUsage(s.getName())));
	}
}
