package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.model.ChangeServiceState;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.host.HostsWSHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class HostImpl implements IHost {
	
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private HostHandler hostHandler;
	@Autowired
	private HostsWSHandler hostsWsHandler;
	@Autowired
	private HostDetailWSHandler hostDetailWsHandler;
	
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IAgentDAO agentDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	@Override
	@Transactional
	public Host[] getHosts() {
		return this.hostDAO.findList().stream().map(h -> h.toApi(this.serviceStateDAO, this.agentDAO, this.packageStateDAO, this.templateDAO)).toArray(Host[]::new);
	}
	
	@Override
	@Transactional
	public SimpleHost[] getSimpleHosts() {
		List<EHost> list = this.hostDAO.findList();
		List<SimpleHost> h = new ArrayList<>();
		for (EHost eHost : list) {
			ETemplate template = this.templateDAO.findById(eHost.getTemplateId());
			EAgent agent = this.agentDAO.findById(eHost.getAgentId());
			//TODO: CREATE COUNTS ON DB
			List<EServiceState> services = this.serviceStateDAO.findByHost(eHost.getId());
			List<EPackageState> packages = this.packageStateDAO.findByHost(eHost.getId());
			SimpleHost sh = new SimpleHost(eHost.getName(), agent.getName(), eHost.getUuid(), template.getName(), eHost.getLastSeen(), (long) services.size(), (long) packages.size());
			h.add(sh);
		}
		return h.toArray(new SimpleHost[0]);
	}
	
	@Override
	@Transactional
	public Host getHost(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost eHost = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(eHost, Response.Status.NOT_FOUND);
		return eHost.toApi(this.serviceStateDAO, this.agentDAO, this.packageStateDAO, this.templateDAO);
	}
	
	@Override
	@Transactional
	public void deleteHost(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost eHost = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(eHost);
		this.hostsWsHandler.broadcastEvent(eHost.getId(), ChangeType.DELETED);
		this.hostDAO.delete(eHost);
	}
	
	@Override
	@Transactional
	public void setServiceState(ChangeServiceState changeServiceState) {
		RESTAssert.assertNotEmpty(changeServiceState.getHostUuid());
		RESTAssert.assertNotEmpty(changeServiceState.getService());
		RESTAssert.assertNotNull(changeServiceState.getTargetState());
		EHost eHost = this.hostDAO.findByUuid(changeServiceState.getHostUuid());
		this.hostHandler.changeServiceState(eHost, changeServiceState.getService(), changeServiceState.getTargetState());
		eHost = this.hostDAO.save(eHost);
		this.hostDetailWsHandler.broadcastChange(changeServiceState.getHostUuid(), new WSChangeEvent<>(ChangeType.UPDATED, eHost.toApi(this.serviceStateDAO, this.agentDAO, this.packageStateDAO, this.templateDAO)));
	}
	
	@Override
	@Transactional
	public void moveHost(String hostUuid, String newTemplate) {
		RESTAssert.assertNotEmpty(hostUuid);
		RESTAssert.assertNotEmpty(newTemplate);
		EHost eHost = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(eHost);
		eHost = this.hostHandler.moveHostToNewTemplate(eHost, newTemplate);
		this.hostDetailWsHandler.broadcastChange(eHost.getUuid(), new WSChangeEvent<>(ChangeType.UPDATED, eHost.toApi(this.serviceStateDAO, this.agentDAO, this.packageStateDAO, this.templateDAO)));
	}
}
