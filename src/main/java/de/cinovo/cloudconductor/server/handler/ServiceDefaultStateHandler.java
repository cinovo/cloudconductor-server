package de.cinovo.cloudconductor.server.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class ServiceDefaultStateHandler {
	
	@Autowired
	private IServiceDefaultStateDAO serviceDefaultStateDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	
	
	/**
	 * @param templateName           the name of the template
	 * @param serviceName            the name of the service
	 * @param newServiceDefaultState the new default state of the given service in the given template
	 * @return the updated default state
	 */
	public EServiceDefaultState updateServiceDefaultState(String templateName, String serviceName, ServiceState newServiceDefaultState) {
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		EService service = this.serviceDAO.findByName(serviceName);
		RESTAssert.assertNotNull(service);
		
		EServiceDefaultState esds = this.serviceDefaultStateDAO.findByServiceAndTemplate(service.getId(), template.getId());
		
		if (esds == null) {
			esds = this.createServiceDefaultState(templateName, serviceName, newServiceDefaultState);
		} else {
			esds.setState(newServiceDefaultState);
			esds = this.serviceDefaultStateDAO.save(esds);
		}
		
		return esds;
	}
	
	private EServiceDefaultState createServiceDefaultState(String templateName, String serviceName, ServiceState state) {
		ETemplate eTemplate = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(eTemplate);
		
		EService eService = this.serviceDAO.findByName(serviceName);
		RESTAssert.assertNotNull(eService);
		
		EServiceDefaultState newEntity = new EServiceDefaultState();
		newEntity.setTemplateId(eTemplate.getId());
		newEntity.setServiceId(eService.getId());
		newEntity.setState(state);
		
		return this.serviceDefaultStateDAO.save(newEntity);
	}
	
}
