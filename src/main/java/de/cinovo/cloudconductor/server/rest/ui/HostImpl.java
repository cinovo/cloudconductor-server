package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.model.ChangeServiceState;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.model.EHost;
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
		List<Host> result = new ArrayList<>();
		for(EHost eHost : this.hostDAO.findList()) {
			result.add(eHost.toApi());
		}
		return result.toArray(new Host[result.size()]);
	}

	@Override
	@Transactional
	public Host getHost(String hostName) {
		RESTAssert.assertNotEmpty(hostName);
		EHost eHost = this.hostDAO.findByName(hostName);
		RESTAssert.assertNotNull(eHost, Response.Status.NOT_FOUND);
		return eHost.toApi();
	}

	@Override
	@Transactional
	public void deleteHost(String hostName) {
		RESTAssert.assertNotEmpty(hostName);
		EHost eHost = this.hostDAO.findByName(hostName);
		this.hostDAO.delete(eHost);
		this.hostsWsHandler.broadcastEvent(new WSChangeEvent<Host>(ChangeType.DELETED, eHost.toApi()));
	}

	@Override
	@Transactional
	public void setServiceState(ChangeServiceState changeServiceState) {
		RESTAssert.assertNotEmpty(changeServiceState.getHost());
		RESTAssert.assertNotEmpty(changeServiceState.getService());
		RESTAssert.assertNotNull(changeServiceState.getTargetState());
		EHost eHost = this.hostDAO.findByName(changeServiceState.getHost());
		this.hostHandler.changeServiceState(eHost, changeServiceState.getService(), changeServiceState.getTargetState());
		eHost = this.hostDAO.save(eHost);
		this.hostDetailWsHandler.broadcastChange(changeServiceState.getHost(), new WSChangeEvent<>(ChangeType.UPDATED, eHost.toApi()));
	}
}
