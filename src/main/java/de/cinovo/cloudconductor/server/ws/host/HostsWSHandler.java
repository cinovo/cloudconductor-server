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
	 * @param eHost the host
	 * @param type  the change type
	 */
	public void broadcastEvent(EHost eHost, ChangeType type) {
		if (eHost == null) {
			return;
		}
		ETemplate template = this.templateDAO.findById(eHost.getTemplateId());
		String agentName = "";
		if(eHost.getAgentId() != null) {
			EAgent agent = this.agentDAO.findById(eHost.getAgentId());
			agentName = agent.getName();
		}
		//TODO: CREATE COUNTS ON DB
		List<EServiceState> services = this.serviceStateDAO.findByHost(eHost.getId());
		List<EPackageState> packages = this.packageStateDAO.findByHost(eHost.getId());
		SimpleHost sh = new SimpleHost(eHost.getName(), agentName, eHost.getUuid(), template.getName(), eHost.getLastSeen(), (long) services.size(), (long) packages.size());
		this.broadcastEvent(new WSChangeEvent<>(type, sh));
	}
	
}
