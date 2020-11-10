package de.cinovo.cloudconductor.server.ws.host;

import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.ASimpleWSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class HostsWSHandler extends ASimpleWSHandler<HostsWSAdapter, SimpleHost> {
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IAgentDAO agentDAO;
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	/**
	 * @param id   the host id
	 * @param type the change type
	 */
	public void broadcastEvent(Long id, ChangeType type) {
		EHost eHost = this.hostDAO.findById(id);
		if (eHost == null) {
			return;
		}
		ETemplate template = this.templateDAO.findById(eHost.getTemplateId());
		EAgent agent = this.agentDAO.findById(eHost.getAgentId());
		//TODO: CREATE COUNTS ON DB
		List<EServiceState> services = this.serviceStateDAO.findByHost(eHost.getId());
		List<EPackageState> packages = this.packageStateDAO.findByHost(eHost.getId());
		SimpleHost sh = new SimpleHost(eHost.getName(), agent.getName(), eHost.getUuid(), template.getName(), eHost.getLastSeen(), (long) services.size(), (long) packages.size());
		this.broadcastEvent(new WSChangeEvent<>(type, sh));
	}
}
