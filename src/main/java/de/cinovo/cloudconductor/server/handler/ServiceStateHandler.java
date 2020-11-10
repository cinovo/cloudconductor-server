package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class ServiceStateHandler {
	
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private HostDetailWSHandler hostDetailWsHandler;
	@Autowired
	private IServiceDAO serviceDao;
	@Autowired
	private IHostDAO hostDAO;
	
	/**
	 * @param serviceState the service state
	 * @param stateChanges the stateChanges
	 * @return the changed service state
	 */
	public EServiceState handleStartedService(EServiceState serviceState, ServiceStatesChanges stateChanges) {
		EService service;
		switch (serviceState.getState()) {
			case RESTARTING_STARTING:
			case STARTING:
				serviceState.nextState();
				EServiceState save = this.serviceStateDAO.save(serviceState);
				// service is now started, inform user interface via WS
				EHost host = this.hostDAO.findById(save.getHostId());
				this.hostDetailWsHandler.broadcastChange(host, ChangeType.UPDATED);
				return save;
			case STOPPING:
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToStop().add(service.getInitScript());
				break;
			case RESTARTING_STOPPING:
				serviceState.nextState();
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToRestart().add(service.getInitScript());
				return this.serviceStateDAO.save(serviceState);
			case STOPPED:
				serviceState.setState(ServiceState.STOPPING);
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToStop().add(service.getInitScript());
				return this.serviceStateDAO.save(serviceState);
			default:
				break;
		}
		return serviceState;
	}
	
	/**
	 * @param serviceState the service state
	 * @param stateChanges the stateChanges
	 */
	public void handleStoppedService(EServiceState serviceState, ServiceStatesChanges stateChanges) {
		EService service;
		switch (serviceState.getState()) {
			case STARTING:
			case RESTARTING_STARTING:
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToStart().add(service.getInitScript());
				break;
			case RESTARTING_STOPPING:
				serviceState.nextState();
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToStart().add(service.getInitScript());
				this.serviceStateDAO.save(serviceState);
				return;
			case STOPPED:
			case STOPPING:
				serviceState.nextState();
				EServiceState save = this.serviceStateDAO.save(serviceState);
				// service is now stopped, inform user interface via WS
				EHost host = this.hostDAO.findById(save.getHostId());
				this.hostDetailWsHandler.broadcastChange(host, ChangeType.UPDATED);
				return;
			case STARTED:
				service = this.serviceDao.findById(serviceState.getServiceId());
				stateChanges.getToStart().add(service.getInitScript());
				serviceState.setState(ServiceState.STARTING);
				this.serviceStateDAO.save(serviceState);
				return;
			default:
				break;
		}
	}
	
}
