package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.enums.TaskState;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.cinovo.cloudconductor.api.model.SimpleHost;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
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
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.host.HostDetailWSHandler;
import de.cinovo.cloudconductor.server.ws.host.HostsWSHandler;
import de.taimos.restutils.RESTAssert;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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


	/**
	 * @param hostName     the name of the host
	 * @param templateName the name of the template
	 * @param rpmState     the package state to be updated
	 * @param uuid         the uuid
	 * @return computed changes for package state
	 */
	@Transactional
	public PackageStateChanges handlePackageState(String hostName, String templateName, PackageState rpmState, String uuid) {
		RESTAssert.assertNotEmpty(templateName);
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);

		RESTAssert.assertNotEmpty(hostName);
		EHost host = this.hostDAO.findByUuid(uuid);
		RESTAssert.assertNotNull(host);

		host.setLastSeen((new DateTime()).getMillis());

		List<EPackage> packages = this.packageDAO.findList();
		HashSet<EPackageState> leftPackages = new HashSet<>(host.getPackages());
		for(PackageVersion installedPV : rpmState.getInstalledRpms()) {
			EPackage knownPackage = packages.stream().filter(p -> p.getName().equals(installedPV.getName())).findFirst().orElse(null);
			if(knownPackage == null) {
				continue;
			}
			EPackageState state = this.packageStateHandler.updateExistingState(host, installedPV, leftPackages);
			if(state == null) {
				state = this.packageStateHandler.createMissingState(host, installedPV, knownPackage);
				host.getPackages().add(state);
			}
		}
		this.packageStateHandler.removePackageState(host, leftPackages);
		host = this.hostDAO.save(host);
		SimpleHost simpleHost = this.hostDAO.findSimpleHost(host.getId());
		this.hostsWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, simpleHost));
		this.hostDetailWsHandler.broadcastChange(hostName, new WSChangeEvent<>(ChangeType.UPDATED, host.toApi()));

		// check whether the host may updateEntity or has to wait for another host to finish updating
		if(this.sendPackageChanges(template, host)) {
			PackageStateChanges diff = this.psChangeHandler.computePackageDiff(host, template);
			if(!diff.getToInstall().isEmpty() || !diff.getToUpdate().isEmpty() || !diff.getToErase().isEmpty()) {
				host.setStartedUpdate(DateTime.now().getMillis());
			}
			return diff;
		}
		return new PackageStateChanges(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	private boolean sendPackageChanges(ETemplate template, EHost host) {
		DateTime now = DateTime.now();
		int maxHostsOnUpdate = template.getHosts().size() / 2;
		int hostsOnUpdate = 0;
		if((template.getSmoothUpdate() == null) || !template.getSmoothUpdate() || (maxHostsOnUpdate < 1)) {
			return true;
		}
		if(host.getStartedUpdate() != null) {
			return true;
		}
		for(EHost h : template.getHosts()) {
			if(h.getStartedUpdate() != null) {
				int timeElapsed = Minutes.minutesBetween(new DateTime(h.getStartedUpdate()), now).getMinutes();
				if(timeElapsed > AgentHandler.MAX_UPDATE_THRESHOLD) {
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
	 * @param hostName     the name of the host
	 * @param templateName the name of the template
	 * @param serviceState the incoming service state
	 * @param uuid         the UUID of the agent
	 * @return the computed changes for the services
	 */
	@Transactional
	public ServiceStatesChanges handleServiceState(String hostName, String templateName, ServiceStates serviceState, String uuid) {
		ETemplate template = this.templateDAO.findByName(templateName);
		RESTAssert.assertNotNull(template);

		EHost host = this.hostDAO.findByUuid(uuid);
		RESTAssert.assertNotNull(host);

		if(this.serviceHandler.assertHostServices(template, host)) {
			host = this.hostDAO.findByUuid(uuid);
		}
		ServiceStatesChanges serviceStatesChanges = new ServiceStatesChanges(new HashSet<>(), new HashSet<>(), new HashSet<>());
		Set<EServiceState> stateList = new HashSet<>(host.getServices());

		// agent sends running services
		for(String sname : serviceState.getRunningServices()) {
			for(EServiceState state : host.getServices()) {
				if(state.getService().getName().equals(sname)) {
					stateList.remove(state);
					this.serviceStateHandler.handleStartedService(state, serviceStatesChanges);
				}
			}
		}

		// remaining elements in stateList refer to services which are not running at the moment
		for(EServiceState hostState : stateList) {
			EServiceState state = this.serviceStateDAO.findById(hostState.getId());
			if(state != null) {
				this.serviceStateHandler.handleStopedService(state, serviceStatesChanges);
			}
		}

		if(serviceStatesChanges.getToStart().isEmpty() && serviceStatesChanges.getToStart().isEmpty() && serviceStatesChanges.getToStart().isEmpty() && (host.getStartedUpdate() != null)) {
			host = this.hostDAO.findById(host.getId());
			host.setStartedUpdate(null);
			this.hostDAO.save(host);
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
		if(agent == null) {
			agent = this.createNewAgent(agentName);
		}

		EHost host = this.getHost(templateName, hostName, uuid);
		host.setLastSeen((new DateTime()).getMillis());
		host.setAgent(agent);
		host = this.hostDAO.save(host);
		SimpleHost simpleHost = this.hostDAO.findSimpleHost(host.getId());
		this.hostsWSHandler.broadcastEvent(new WSChangeEvent<>(ChangeType.UPDATED, simpleHost));

		EAgentOption options = this.agentOptionsDAO.findByTemplate(host.getTemplate());
		if(options == null) {
			options = new EAgentOption();
			options.setTemplate(host.getTemplate());
			options = this.agentOptionsDAO.save(options);
		}
		AgentOption result = options.toApi();
		result.setTemplateName(host.getTemplate().getName());
		result.setUuid(host.getUuid());
		boolean onceExecuted = false;
		if(options.getDoSshKeys() == TaskState.ONCE) {
			if(host.getExecutedSSH()) {
				result.setDoSshKeys(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedSSH(true);
			}
		}
		if(options.getDoPackageManagement() == TaskState.ONCE) {
			if(host.getExecutedPkg()) {
				result.setDoPackageManagement(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedPkg(true);
			}
		}
		if(options.getDoFileManagement() == TaskState.ONCE) {
			if(host.getExecutedFiles()) {
				result.setDoFileManagement(TaskState.OFF);
			} else {
				onceExecuted = true;
				host.setExecutedFiles(true);
			}
		}
		if(onceExecuted) {
			this.hostDAO.save(host);
		}

		return result;
	}

	private EAgent createNewAgent(String agentName) {
		EUser currentUser = this.userHandler.getCurrentUser();
		if(currentUser == null) {
			return null;
		}
		EAgent agent = new EAgent();
		agent.setName(agentName);
		agent.setUser(currentUser);
		return this.agentDAO.save(agent);
	}

	private EHost getHost(String templateName, String hostName, String uuid) {
		EHost host = null;
		if(uuid != null && !uuid.isEmpty()) {
			host = this.hostDAO.findByUuid(uuid);
		}
		if(host == null) {
			EHost temphost = this.hostDAO.findByName(hostName);
			if(temphost != null && (temphost.getUuid() == null || temphost.getUuid().isEmpty())) {
				host = temphost;
				host.setUuid(UUID.randomUUID().toString());
				host = this.hostDAO.save(host);
			}
		}
		if(host == null) {
			host = this.hostHandler.createNewHost(hostName, this.templateDAO.findByName(templateName));
		}
		return host;
	}

}
