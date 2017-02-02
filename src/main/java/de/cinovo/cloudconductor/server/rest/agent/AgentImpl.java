package de.cinovo.cloudconductor.server.rest.agent;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * <p>
 * TODO: REWORK
 *
 * @author psigloch
 * @deprecated NEEDS MASSIVE REWORK
 */
@Deprecated
@JaxRsComponent
public class AgentImpl {
/*
	private static final int MAX_TIMEOUT_HOST = 30;

	private static final int MAX_UPDATE_THRESHOLD = 15;

	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IServiceDAO serviceDAO;
	@Autowired
	private IServiceStateDAO serviceStateDAO;
	@Autowired
	private IPackageVersionDAO versionDAO;
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageStateDAO packageStateDAO;
	@Autowired
	private IServiceDefaultStateDAO serviceDefaultStateDAO;
	@Autowired
	private IAgentOptionsDAO agentOptionsDAO;
	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IAgentDAO agentDAO;


	@Transactional
	public Set<String> getAliveAgents() {
		List<EHost> hosts = this.hostDAO.findList();
		DateTime now = new DateTime();
		Set<String> result = new HashSet<>();
		for(EHost host : hosts) {
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();

			if(diff < AgentImpl.MAX_TIMEOUT_HOST) {
				result.add(host.getName());
			}
		}
		return result;
	}

	
	@Transactional
	public PackageStateChanges notifyPackageState(String tname, String hname, PackageState rpmState) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		EHost host = this.hostDAO.findByName(hname);
		ETemplate template = this.templateDAO.findByName(tname);
		RESTAssert.assertNotNull(template);

		if(host == null) {
			host = this.createNewHost(hname, template);
		}
		DateTime now = new DateTime();
		host.setLastSeen(now.getMillis());
		List<EPackage> packages = this.packageDAO.findList();
		HashSet<EPackageState> leftPackages = new HashSet<>(host.getPackages());
		for(PackageVersion irpm : rpmState.getInstalledRpms()) {
			EPackage pkg = null;
			for(EPackage p : packages) {
				if(p.getName().equals(irpm.getName())) {
					pkg = p;
					break;
				}
			}
			if(pkg == null) {
				continue;
			}
			EPackageState state = this.updateExistingState(host, irpm, leftPackages);
			if(state == null) {
				state = this.createMissingState(host, irpm, pkg);
				host.getPackages().add(state);
			}

		}
		for(EPackageState pkg : leftPackages) {
			if(host.getPackages().contains(pkg)) {
				host.getPackages().remove(pkg);
				this.packageStateDAO.delete(pkg);
			}
		}
		host = this.hostDAO.save(host);

		// check whether the host may updateEntity or has to wait for another host to finish updateing
		if(this.sendPackageChanges(template, host)) {

			// Compute instruction lists (install/updateEntity/erase) from difference between packages actually installed packages that should be
			// installed.
			Set<EPackageVersion> actual = new HashSet<>();
			for(EPackageState state : host.getPackages()) {
				actual.add(state.getVersion());
			}
			ArrayListMultimap<PackageCommand, PackageVersion> diff = this.computePackageDiff(template.getPackageVersions(), actual);
			if(!diff.get(PackageCommand.INSTALL).isEmpty() || !diff.get(PackageCommand.UPDATE).isEmpty() || !diff.get(PackageCommand.ERASE).isEmpty()) {
				host.setStartedUpdate(DateTime.now().getMillis());
			}
			return new PackageStateChanges(diff.get(PackageCommand.INSTALL), diff.get(PackageCommand.UPDATE), diff.get(PackageCommand.ERASE));
		}
		return new PackageStateChanges(new ArrayList<PackageVersion>(), new ArrayList<PackageVersion>(), new ArrayList<PackageVersion>());
	}

	private EHost createNewHost(String hname, ETemplate template) {
		EHost host;
		host = new EHost();
		host.setName(hname);
		host.setTemplate(template);
		host = this.hostDAO.save(host);
		return host;
	}

	*//**
	 * @param template
	 * @param host
	 *//*
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
				if(timeElapsed > AgentImpl.MAX_UPDATE_THRESHOLD) {
					continue;
				}
				hostsOnUpdate++;
			}
		}
		if(maxHostsOnUpdate > hostsOnUpdate) {
			return true;
		}
		return false;
	}

	*//**
	 * @param host
	 * @param irpm
	 * @param pkg
	 * @return
	 *//*
	private EPackageState createMissingState(EHost host, PackageVersion irpm, EPackage pkg) {
		EPackageState state;
		EPackageVersion rpm = this.versionDAO.find(irpm.getName(), irpm.getVersion());
		if(rpm == null) {
			rpm = new EPackageVersion();
			rpm.setPkg(pkg);
			rpm.setVersion(irpm.getVersion());
			rpm.setDeprecated(true);
			//rpm.getRepos().add(this.repoDAO.findByName(irpm.getRepos()));
			rpm = this.versionDAO.save(rpm);
		}
		state = new EPackageState();
		state.setHost(host);
		state.setVersion(rpm);
		state = this.packageStateDAO.save(state);
		return state;
	}

	*//**
	 * @param host
	 * @param irpm
	 * @param leftPackages
	 * @return
	 *//*
	private EPackageState updateExistingState(EHost host, PackageVersion irpm, HashSet<EPackageState> leftPackages) {
		VersionStringComparator vsc = new VersionStringComparator();
		for(EPackageState state : host.getPackages()) {
			if(state.getVersion().getPkg().getName().equals(irpm.getName())) {
				int comp = vsc.compare(state.getVersion().getVersion(), irpm.getVersion());
				if(comp == 0) {
					break;
				}
				EPackageVersion rpm = this.versionDAO.find(irpm.getName(), irpm.getVersion());
				if(rpm == null) {
					rpm = new EPackageVersion();
					rpm.setPkg(state.getVersion().getPkg());
					rpm.setVersion(irpm.getVersion());
					rpm.setDeprecated(true);
					rpm = this.versionDAO.save(rpm);
				}
				leftPackages.remove(state);
				state.setVersion(rpm);
				return this.packageStateDAO.save(state);
			}
		}
		return null;
	}

	private boolean asserHostServices(ETemplate template, EHost host) {
		List<EService> services = this.serviceDAO.findList();
		Set<EService> templateServices = new HashSet<>();
		for(EService s : services) {
			for(EPackageVersion p : template.getPackageVersions()) {
				if(s.getPackages().contains(p.getPkg())) {
					templateServices.add(s);
				}
			}
		}
		Set<EService> missingServices = new HashSet<>(templateServices);
		Set<EServiceState> nonUsedServiceStates = new HashSet<>(host.getServices());
		for(EServiceState state : host.getServices()) {
			for(EService service : templateServices) {
				if(service.getName().equals(state.getService().getName())) {
					missingServices.remove(service);
					for(EServiceState ss : nonUsedServiceStates) {
						if(ss.getService().getId().equals(service.getId())) {
							nonUsedServiceStates.remove(ss);
							break;
						}
					}
					break;
				}
			}
		}
		boolean changes = false;
		// add new service states
		for(EService service : missingServices) {
			EServiceState state = new EServiceState();
			state.setService(service);
			state.setHost(host);

			EServiceDefaultState dss = this.serviceDefaultStateDAO.findByName(service.getName(), template.getName());
			if((dss != null)) {
				state.setState(dss.getState());
			}

			this.serviceStateDAO.save(state);
			changes = true;
		}
		// clean up old no more used service states
		for(EServiceState ss : nonUsedServiceStates) {
			this.serviceStateDAO.delete(ss);
		}
		return changes;
	}

	
	@Transactional
	public ServiceStatesChanges notifyServiceState(String tname, String hname, ServiceStates serviceState) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		EHost host = this.hostDAO.findByName(hname);
		ETemplate template = this.templateDAO.findByName(tname);
		RESTAssert.assertNotNull(template);
		if(host == null) {
			host = this.createNewHost(hname, template);
		}
		if(this.asserHostServices(template, host)) {
			host = this.hostDAO.findByName(hname);
		}

		Set<String> toStop = new HashSet<>();
		Set<String> toStart = new HashSet<>();
		Set<String> toRestart = new HashSet<>();

		Set<EServiceState> stateList = new HashSet<>(host.getServices());
		// agent sends running services
		for(String sname : serviceState.getRunningServices()) {
			for(EServiceState state : host.getServices()) {
				if(state.getService().getName().equals(sname)) {
					stateList.remove(state);
					switch(state.getState()) {
						case RESTARTING_STARTING:
						case STARTING:
							state.nextState();
							this.serviceStateDAO.save(state);
							break;
						case STOPPING:
							toStop.add(state.getService().getInitScript());
							break;
						case RESTARTING_STOPPING:
							toRestart.add(state.getService().getInitScript());
							state.nextState();
							this.serviceStateDAO.save(state);
							break;
						case STOPPED:
							state.nextState();
							toStop.add(state.getService().getInitScript());
							this.serviceStateDAO.save(state);
							break;
						default:
							break;
					}
				}
			}
		}

		// agent sends stopped services
		for(EServiceState state : stateList) {

			switch(state.getState()) {
				case STARTING:
					toStart.add(state.getService().getInitScript());
					break;
				case STOPPING:
					state.nextState();
					this.serviceStateDAO.save(state);
					break;
				case STARTED:
					toStart.add(state.getService().getInitScript());
					state.setState(ServiceState.STARTING);
					this.serviceStateDAO.save(state);
					break;
				default:
					break;

			}

		}

		HashSet<ConfigFile> configFiles = new HashSet<>();
		for(EFile file : template.getConfigFiles()) {
			configFiles.add(file.toApi());
		}

		if(toStart.isEmpty() && toStop.isEmpty() && toRestart.isEmpty() && (host.getStartedUpdate() != null)) {
			host.setStartedUpdate(null);
			this.hostDAO.save(host);
		}
		ServiceStatesChanges serviceStatesChanges = new ServiceStatesChanges(toStart, toStop, toRestart);
		serviceStatesChanges.setConfigFiles(configFiles);
		return serviceStatesChanges;
	}

	
	@Transactional
	public AgentOptions heartBeat(String tname, String hname, String agentN) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		RESTAssert.assertNotEmpty(agentN);
		EAgent agent = this.agentDAO.findAgentByName(agentN);
		if(agent == null) {
			agent = this.createNewAgent(agentN, null);
		}
		EHost host = this.hostDAO.findByName(hname);
		if(host == null) {
			host = this.createNewHost(hname, this.templateDAO.findByName(tname));
		}
		DateTime now = new DateTime();
		host.setLastSeen(now.getMillis());
		host.setAgent(agent);
		host = this.hostDAO.save(host);

		EAgentOption options = this.agentOptionsDAO.findByTemplate(host.getTemplate());
		if(options == null) {
			options = new EAgentOption();
			options.setTemplate(host.getTemplate());
			options = this.agentOptionsDAO.save(options);
		}
		AgentOption result = options.toApi();
		result.setTemplateName(host.getTemplate().getName());
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
		//TODO BROKEN DUE TO INTERFACE CHANGES
		return null;
	}

	private EAgent createNewAgent(String agentName, EAgentAuthToken authToken) {
		EAgent agent = new EAgent();
		agent.setName(agentName);
		if(authToken != null) {
			agent.setToken(authToken);
			agent.setTokenAssociationDate(DateTime.now().getMillis());
		}
		return this.agentDAO.save(agent);
	}

	private ArrayListMultimap<PackageCommand, PackageVersion> computePackageDiff(Collection<EPackageVersion> nominal, Collection<EPackageVersion> actual) {
		// Determine which package versions need to be erased and which are to be installed.
		TreeSet<EPackageVersion> toInstall = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toInstall.addAll(nominal);
		toInstall.removeAll(actual);
		TreeSet<EPackageVersion> toErase = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toErase.addAll(actual);
		toErase.removeAll(nominal);

		// Resolve the removal of an older version and the installation of a newer one to an updateEntity instruction.
		TreeSet<EPackageVersion> toUpdate = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		for(EPackageVersion i : toInstall) {
			EPackageVersion e = toErase.lower(i);
			if((e != null) && e.getPkg().getName().equals(i.getPkg().getName())) {
				toErase.remove(e);
				toUpdate.add(i);
			}
		}
		toInstall.removeAll(toUpdate);

		// get rid of reserved packages on erase
		Set<EPackageVersion> keep = new HashSet<>();
		EServerOptions eServerOptions = this.serverOptionsDAO.get();
		for(String pkg : eServerOptions.getDisallowUninstall()) {
			for(EPackageVersion erase : toErase) {
				if(erase.getPkg().getName().equals(pkg)) {
					keep.add(erase);
					break;
				}
			}
		}
		toErase.removeAll(keep);

		// Convert the lists of package versions to lists of RPM descriptions (RPM name, release, and version).
		ArrayListMultimap<PackageCommand, PackageVersion> result = ArrayListMultimap.create();
		result = this.fillPackageDiff(result, PackageCommand.INSTALL, toInstall);
		result = this.fillPackageDiff(result, PackageCommand.UPDATE, toUpdate);
		result = this.fillPackageDiff(result, PackageCommand.ERASE, toErase);
		return result;
	}

	private ArrayListMultimap<PackageCommand, PackageVersion> fillPackageDiff(ArrayListMultimap<PackageCommand, PackageVersion> map, PackageCommand command, Collection<EPackageVersion> packageVersions) {
		for(EPackageVersion pv : packageVersions) {
			String rpmName = pv.getPkg().getName();
			Set<Dependency> dep = new HashSet<>();
			for(EDependency edep : pv.getDependencies()) {
				dep.add(edep.toApi());
			}
			PackageVersion apiPV = new PackageVersion();
			apiPV.setName(rpmName);
			apiPV.setVersion(pv.getVersion());
			apiPV.setDependencies(dep);
			map.put(command, apiPV);
		}
		return map;
	}

	
	@Transactional
	public boolean isServerAlive() {
		return true;
	}*/
}
