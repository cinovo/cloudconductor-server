package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.IHost;
import de.cinovo.cloudconductor.api.model.Host;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.handler.HostHandler;
import de.cinovo.cloudconductor.server.model.EHost;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
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


	@Override
	@Transactional
	public List<Host> getHosts() {
		List<Host> result = new ArrayList<>();
		for(EHost eHost : this.hostDAO.findList()) {
			result.add(eHost.toApi());
		}
		return result;
	}

	@Override
	@Transactional
	public Host getHost(String hostName) {
		RESTAssert.assertNotEmpty(hostName);
		EHost eHost = this.hostDAO.findByName(hostName);
		RESTAssert.assertNotNull(eHost);
		return eHost.toApi();
	}

	@Override
	@Transactional
	public void deleteHost(String hostName) {
		RESTAssert.assertNotEmpty(hostName);
		EHost eHost = this.hostDAO.findByName(hostName);
		this.hostDAO.delete(eHost);
	}

	@Override
	@Transactional
	public void setServiceState(String hostName, String serviceName, ServiceState newState) {
		RESTAssert.assertNotEmpty(hostName);
		RESTAssert.assertNotEmpty(serviceName);
		RESTAssert.assertNotNull(newState);
		EHost eHost = this.hostDAO.findByName(hostName);
		this.hostHandler.changeServiceState(eHost, serviceName, newState);
		this.hostDAO.save(eHost);
	}
}
