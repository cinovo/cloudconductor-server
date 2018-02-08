package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.model.ChangeServiceState;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.host.HostsWSHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class HostImpl implements IHost {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private HostHandler hostHandler;
	@Autowired
	private HostsWSHandler hostsWsHandler;
	@Autowired
	private HostDetailWSHandler hostDetailWsHandler;


	@Override
	@Transactional
	public Host[] getHosts() {
		return this.hostDAO.findList().stream().map(EHost::toApi).toArray(Host[]::new);
	}

	@Override
	public SimpleHost[] getSimpleHosts() {
		List<SimpleHost> simpleHosts = this.hostDAO.findSimpleHosts();
		return simpleHosts.toArray(new SimpleHost[0]);
	}

	@Override
	@Transactional
	public Host getHost(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost eHost = this.hostDAO.findByUuid(hostUuid);
		RESTAssert.assertNotNull(eHost, Response.Status.NOT_FOUND);
		return eHost.toApi();
	}

	@Override
	@Transactional
	public void deleteHost(String hostUuid) {
		RESTAssert.assertNotEmpty(hostUuid);
		EHost eHost = this.hostDAO.findByUuid(hostUuid);
		this.hostDAO.delete(eHost);
		this.hostsWsHandler.broadcastEvent(new WSChangeEvent<Host>(ChangeType.DELETED, eHost.toApi()));
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
		this.hostDetailWsHandler.broadcastChange(changeServiceState.getHostUuid(), new WSChangeEvent<>(ChangeType.UPDATED, eHost.toApi()));
	}
}
