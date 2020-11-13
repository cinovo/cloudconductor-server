package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.TaskState;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.security.AuthHandler;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.host.HostsWSHandler;
import de.taimos.restutils.RESTAssert;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class AgentHandler {
	private final Logger logger = LoggerFactory.getLogger(AgentHandler.class);
	private static final int MAX_UPDATE_THRESHOLD = 15;
	
	@Autowired
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	
	@Autowired
	private ServiceStateHandler serviceStateHandler;
	@Autowired
	private HostHandler hostHandler;
	@Autowired
	private PackageStateHandler packageStateHandler;
	@Autowired
	private PackageStateChangeHandler psChangeHandler;
	@Autowired
	private ServiceHandler serviceHandler;
	@Autowired
	private FileHandler fileHandler;
	
	@Autowired
	private HostsWSHandler hostsWSHandler;
	@Autowired
	private HostDetailWSHandler hostDetailWsHandler;
	@Autowired
	private IAgentDAO agentDAO;
	@Autowired
	private AuthHandler userHandler;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	
	/**
	 * @param hostName     the name of the host
	 * @param templateName the name of the template
	 * @param rpmState     the package state to be updated
	 * @param uuid         the uuid
	 * @return computed changes for package state
	 */
	@Transactional
	public PackageStateChanges handlePackageState(String hostName, String templateName, PackageState rpmState, String uuid) {
		RESTAssert.assertNotEmpty(hostName);
		EHost host = this.hostDAO.findByUuid(uuid);
		RESTAssert.assertNotNull(host);
		host.setLastSeen((new DateTime()).getMillis());
		host = this.hostDAO.save(host);
		List<EPackage> packages = this.packageDAO.findList();
		List<EPackageState> leftPackages = this.packageStateDAO.findByHost(host.getId());
		for (PackageVersion installedPV : rpmState.getInstalledRpms()) {
			EPackage knownPackage = packages.stream().filter(p -> p.getName().equals(installedPV.getName())).findFirst().orElse(null);
			if (knownPackage == null) {
				continue;
			}
			EPackageState state = this.packageStateHandler.updateExistingState(host, installedPV, leftPackages);
			if (state == null) {
				this.packageStateHandler.createMissingState(host, installedPV, knownPackage);
			}
		}
		this.packageStateHandler.removePackageState(host, leftPackages);
		this.hostDetailWsHandler.broadcastChange(host, ChangeType.UPDATED);
		this.hostsWSHandler.broadcastEvent(host, ChangeType.UPDATED);
		// check whether the host may updateEntity or has to wait for another host to finish updating
		if (this.sendPackageChanges(host)) {
			PackageStateChanges diff = this.psChangeHandler.computePackageDiff(host);
			if (!diff.getToInstall().isEmpty() || !diff.getToUpdate().isEmpty() || !diff.getToErase().isEmpty()) {
				host.setStartedUpdate(DateTime.now().getMillis());
			}
			return diff;
		}
		return new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}
	
	private boolean sendPackageChanges(EHost host) {
		DateTime now = DateTime.now();
		ETemplate template = this.templateDAO.findById(host.getTemplateId());
		long maxHostsOnUpdate = this.hostDAO.countForTemplate(host.getTemplateId()) / 2;
		long hostsOnUpdate = 0;
		if ((template.getSmoothUpdate() == null) || !template.getSmoothUpdate() || (maxHostsOnUpdate < 1)) {
			return true;
		}
		if (host.getStartedUpdate() != null) {
			return true;
		}
		for (EHost h : this.hostDAO.findHostsForTemplate(template.getId())) {
			if (h.getStartedUpdate() != null) {
				int timeElapsed = Minutes.minutesBetween(new DateTime(h.getStartedUpdate()), now).getMinutes();
				if (timeElapsed > AgentHandler.MAX_UPDATE_THRESHOLD) {
					continue;
				}
				hostsOnUpdate++;
			}
		}
		return maxHostsOnUpdate > hostsOnUpdate;
	}
	
	/**
	 * Handle incoming service states and compute changes.
	 *
	 * @param templateName the name of the template
	 * @param serviceState the incoming service state
	 * @param uuid         the UUID of the agent
	 * @return the computed changes for the services
	 */
	@Transactional
	public ServiceStatesChanges handleServiceState(String templateName, ServiceStates serviceState, String uuid) {
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);
		
		EHost host = this.hostDAO.findByUuid(uuid);
		RESTAssert.assertNotNull(host);
		
		if (this.serviceHandler.assertHostServices(template, host)) {
			host = this.hostDAO.findByUuid(uuid);
		}
		ServiceStatesChanges serviceStatesChanges = new ServiceStatesChanges(new HashSet<>(), new HashSet<>(), new HashSet<>());
		List<EServiceState> hostServiceStates = this.serviceStateDAO.findByHost(host.getId());
		Set<EServiceState> stateList = new HashSet<>(hostServiceStates);
		
		// agent sends running services
		for (String sname : serviceState.getRunningServices()) {
			for (EServiceState state : hostServiceStates) {
				if (state.getServiceName().equals(sname)) {
					stateList.remove(state);
					this.serviceStateHandler.handleStartedService(state, serviceStatesChanges);
				}
			}
		}
		
		// remaining elements in stateList refer to services which are not running at the moment
		for (EServiceState hostState : stateList) {
			EServiceState state = this.serviceStateDAO.findById(hostState.getId());
			if (state != null) {
				this.serviceStateHandler.handleStoppedService(state, serviceStatesChanges);
			}
		}
		
		if (serviceStatesChanges.getToStart().isEmpty() && serviceStatesChanges.getToStop().isEmpty() && serviceStatesChanges.getToRestart().isEmpty() && (host.getStartedUpdate() != null)) {
			host.setStartedUpdate(null);
			host = this.hostDAO.save(host);
			this.hostDetailWsHandler.broadcastChange(host, ChangeType.UPDATED);
		}
		
		HashSet<ConfigFile> configFiles = new HashSet<>();
		Collections.addAll(configFiles, this.fileHandler.getFilesForTemplate(templateName));
		serviceStatesChanges.setConfigFiles(configFiles);
		return serviceStatesChanges;
	}
	
	/**
	 * Handle heart beat coming from an agent.
	 *
	 * @param templateName the name of the template
	 * @param hostName     the name of the host
	 * @param agentName    the name of the agent
	 * @param uuid         the UUID of the agent
	 * @return AgentOption for the agent
	 */
	@Transactional
	public AgentOption handleHeartBeat(String templateName, String hostName, String agentName, String uuid) {
		EAgent agent = this.agentDAO.findAgentByName(agentName);
		if (agent == null) {
			agent = this.createNewAgent(agentName);
		}
		
		EHost host = this.getHost(templateName, hostName, uuid, agent);
		host.setLastSeen((new DateTime()).getMillis());
		if (agent != null) {
			host.setAgentId(agent.getId());
		}
		host = this.hostDAO.save(host);
		this.hostDetailWsHandler.broadcastChange(host, ChangeType.UPDATED);
		this.hostsWSHandler.broadcastEvent(host, ChangeType.UPDATED);
		
		EAgentOption options = this.agentOptionsDAO.findByTemplate(host.getTemplateId());
		if (options == null) {
			options = new EAgentOption();
			options.setTemplateid(host.getTemplateId());
			options = this.agentOptionsDAO.save(options);
		}
		ETemplate template = this.templateDAO.findById(host.getTemplateId());
		AgentOption result = options.toApi(template);
		result.setUuid(host.getUuid());
		
		boolean onceExecuted = false;
		if (options.getDoSshKeys() == TaskState.ONCE) {
			if (host.getExecutedSSH()) {
				result.setDoSshKeys(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedSSH(true);
			}
		}
		if (options.getDoPackageManagement() == TaskState.ONCE) {
			if (host.getExecutedPkg()) {
				result.setDoPackageManagement(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedPkg(true);
			}
		}
		if (options.getDoFileManagement() == TaskState.ONCE) {
			if (host.getExecutedFiles()) {
				result.setDoFileManagement(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedFiles(true);
			}
		}
		if (onceExecuted) {
			this.hostDAO.save(host);
		}
		
		return result;
	}
	
	private EAgent createNewAgent(String agentName) {
		EUser currentUser = this.userHandler.getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		EAgent agent = new EAgent();
		agent.setName(agentName);
		agent.setUserid(currentUser.getId());
		return this.agentDAO.save(agent);
	}
	
	private EHost getHost(String templateName, String hostName, String uuid, EAgent agent) {
		EHost host = null;
		if ((uuid != null) && !uuid.isEmpty()) {
			host = this.hostDAO.findByUuid(uuid);
		}
		if (host == null) {
			EHost temphost = this.hostDAO.findByName(hostName);
			if ((temphost != null) && ((temphost.getUuid() == null) || temphost.getUuid().isEmpty())) {
				host = temphost;
				host.setUuid(UUID.randomUUID().toString());
				host = this.hostDAO.save(host);
			}
		}
		
		ETemplate template = this.templateDAO.findByName(templateName);
		if (host == null) {
			host = this.hostHandler.createNewHost(hostName, template, agent);
		}
		return host;
	}
	
}
