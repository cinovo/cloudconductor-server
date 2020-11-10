package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Service;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.restutils.RESTAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private ITemplateDAO templateDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
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
		this.fillFields(es, s);
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
		this.fillFields(es, s);
		RESTAssert.assertNotNull(es);
		return this.serviceDAO.save(es);
	}
	
	private void fillFields(EService es, Service s) {
		es.setName(s.getName());
		es.setDescription(s.getDescription());
		es.setInitScript(s.getInitScript());
		if ((s.getPackages() != null) && !s.getPackages().isEmpty()) {
			es.setPackages(this.packageDAO.findByName(s.getPackages()).stream().map(EPackage::getId).collect(Collectors.toList()));
		} else {
			es.setPackages(null);
		}
	}
	
	/**
	 * @param template the template
	 * @param host     the host
	 * @return true if there are services missing, false otherwise
	 */
	public boolean assertHostServices(ETemplate template, EHost host) {
		// first find out which services are needed
		Set<EService> templateServices = new HashSet<>(this.findByTemplate(template));
		ServiceHandler.LOGGER.debug("Found " + templateServices.size() + " services for template '" + template.getName() + "' on  host '" + host.getName() + "'");
		
		Set<EService> missingServices = new HashSet<>(templateServices);
		List<EServiceState> ssForHost = this.serviceStateDAO.findByHost(host.getId());
		Set<EServiceState> nonUsedServiceStates = new HashSet<>(ssForHost);
		for (EServiceState state : ssForHost) {
			for (EService service : templateServices) {
				if (service.getId().equals(state.getServiceId())) {
					missingServices.remove(service);
					for (EServiceState ss : nonUsedServiceStates) {
						if (ss.getServiceId().equals(service.getId())) {
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
			state.setServiceId(service.getId());
			state.setServiceName(service.getName());
			state.setHostId(host.getId());
			
			EServiceDefaultState dss = this.serviceDefaultStateDAO.findByServiceAndTemplate(service.getId(), template.getId());
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
	 * @param template the template
	 * @return services of template
	 */
	public Set<EService> findByTemplate(ETemplate template) {
		List<EPackageVersion> pkvs = this.packageVersionDAO.findByIds(template.getPackageVersions());
		Set<EService> result = new HashSet<>();
		for (EPackageVersion pkv : pkvs) {
			result.addAll( this.serviceDAO.findByPackage(pkv.getPkgId()));
		}
		return result;
	}
	
	/**
	 * @param serviceName the name of the service
	 * @return service usage map
	 */
	@Transactional
	public Map<String, String> getServiceUsage(String serviceName) {
		RESTAssert.assertNotEmpty(serviceName);
		EService service = this.serviceDAO.findByName(serviceName);
		RESTAssert.assertNotNull(service);
		
		List<EPackage> servicePackages = this.packageDAO.findByIds(service.getPackages());
		Map<String, String > res = new HashMap<>();
		List<ETemplate> templates = this.templateDAO.findList();
		for (EPackage servicePackage : servicePackages) {
			List<Long> pvs = this.packageVersionDAO.findByPackage(servicePackage).stream().map(EPackageVersion::getId).collect(Collectors.toList());
			for (ETemplate template : templates) {
				if(template.getPackageVersions().stream().anyMatch(pvs::contains)) {
					res.put(template.getName(), servicePackage.getName());
					break;
				}
			}
		}
		return res;
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
