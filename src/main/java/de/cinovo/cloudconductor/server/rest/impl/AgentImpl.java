package de.cinovo.cloudconductor.server.rest.impl;

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

import java.util.*;

import de.cinovo.cloudconductor.server.util.AuthTokenGenerator;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ArrayListMultimap;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.api.interfaces.IAgent;
import de.cinovo.cloudconductor.api.model.AgentOptions;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.api.model.ServiceStatesChanges;
import de.cinovo.cloudconductor.api.model.TaskState;
import de.cinovo.cloudconductor.server.comparators.PackageVersionComparator;
import de.cinovo.cloudconductor.server.comparators.VersionStringComparator;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.dao.IAgentOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageStateDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.model.EAgentOption;
import de.cinovo.cloudconductor.server.model.EDependency;
import de.cinovo.cloudconductor.server.model.EFile;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.model.EService;
import de.cinovo.cloudconductor.server.model.EServiceDefaultState;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.model.enums.PackageCommand;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.JaxRsComponent;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@JaxRsComponent
public class AgentImpl implements IAgent {
	
	private static final int MAX_TIMEOUT_HOST = 30;
	
	private static final int MAX_UPDATE_THRESHOLD = 15;
	
	@Autowired
	private IHostDAO dhost;
	@Autowired
	private ITemplateDAO dtemplate;
	@Autowired
	private IServiceDAO dsvc;
	@Autowired
	private IServiceStateDAO dsvcstate;
	@Autowired
	private IPackageVersionDAO drpm;
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private IPackageStateDAO dpkgstate;
	@Autowired
	private IServiceDefaultStateDAO ddefss;
	@Autowired
	private IAgentOptionsDAO dagentoptions;
	@Autowired
	private IServerOptionsDAO dserveroptions;
	@Autowired
	private IAgentDAO dAgent;

	private static final Logger LOGGER = LoggerFactory.getLogger(AgentImpl.class);

	
	
	@Override
	@Transactional
	public Set<String> getAliveAgents() {
		List<EHost> hosts = this.dhost.findList();
		DateTime now = new DateTime();
		Set<String> result = new HashSet<>();
		for (EHost host : hosts) {
			DateTime dt = new DateTime(host.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			
			if (diff < AgentImpl.MAX_TIMEOUT_HOST) {
				result.add(host.getName());
			}
		}
		return result;
	}
	
	@Override
	@Transactional
	public PackageStateChanges notifyPackageState(String tname, String hname, PackageState rpmState, String huuid) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		EHost host = this.getHost(hname, tname, huuid);
		ETemplate template = this.dtemplate.findByName(tname);
		RESTAssert.assertNotNull(template);

		DateTime now = new DateTime();
		host.setLastSeen(now.getMillis());
		List<EPackage> packages = this.dpkg.findList();
		HashSet<EPackageState> leftPackages = new HashSet<>(host.getPackages());
		for (PackageVersion irpm : rpmState.getInstalledRpms()) {
			EPackage pkg = null;
			for (EPackage p : packages) {
				if (p.getName().equals(irpm.getName())) {
					pkg = p;
					break;
				}
			}
			if (pkg == null) {
				continue;
			}
			EPackageState state = this.updateExistingState(host, irpm, leftPackages);
			if (state == null) {
				state = this.createMissingState(host, irpm, pkg);
				host.getPackages().add(state);
			}
			
		}
		for (EPackageState pkg : leftPackages) {
			if (host.getPackages().contains(pkg)) {
				host.getPackages().remove(pkg);
				this.dpkgstate.delete(pkg);
			}
		}
		host = this.dhost.save(host);
		
		// check whether the host may update or has to wait for another host to finish updateing
		if (this.sendPackageChanges(template, host)) {
			
			// Compute instruction lists (install/update/erase) from difference between packages actually installed packages that should be
			// installed.
			Set<EPackageVersion> actual = new HashSet<>();
			for (EPackageState state : host.getPackages()) {
				actual.add(state.getVersion());
			}
			ArrayListMultimap<PackageCommand, PackageVersion> diff = this.computePackageDiff(template.getPackageVersions(), actual);
			if (!diff.get(PackageCommand.INSTALL).isEmpty() || !diff.get(PackageCommand.UPDATE).isEmpty() || !diff.get(PackageCommand.ERASE).isEmpty()) {
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
		host = this.dhost.save(host);
		return host;
	}
	
	/**
	 * @param template
	 * @param host
	 */
	private boolean sendPackageChanges(ETemplate template, EHost host) {
		DateTime now = DateTime.now();
		int maxHostsOnUpdate = template.getHosts().size() / 2;
		int hostsOnUpdate = 0;
		if ((template.getSmoothUpdate() == null) || !template.getSmoothUpdate() || (maxHostsOnUpdate < 1)) {
			return true;
		}
		if (host.getStartedUpdate() != null) {
			return true;
		}
		for (EHost h : template.getHosts()) {
			if (h.getStartedUpdate() != null) {
				int timeElapsed = Minutes.minutesBetween(new DateTime(h.getStartedUpdate()), now).getMinutes();
				if (timeElapsed > AgentImpl.MAX_UPDATE_THRESHOLD) {
					continue;
				}
				hostsOnUpdate++;
			}
		}
		if (maxHostsOnUpdate > hostsOnUpdate) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param host
	 * @param irpm
	 * @param pkg
	 * @return
	 */
	private EPackageState createMissingState(EHost host, PackageVersion irpm, EPackage pkg) {
		EPackageState state;
		EPackageVersion rpm = this.drpm.find(irpm.getName(), irpm.getVersion());
		if (rpm == null) {
			rpm = new EPackageVersion();
			rpm.setPkg(pkg);
			rpm.setVersion(irpm.getVersion());
			rpm.setDeprecated(true);
			rpm = this.drpm.save(rpm);
		}
		state = new EPackageState();
		state.setHost(host);
		state.setVersion(rpm);
		state = this.dpkgstate.save(state);
		return state;
	}
	
	/**
	 * @param host
	 * @param irpm
	 * @param leftPackages
	 * @return
	 */
	private EPackageState updateExistingState(EHost host, PackageVersion irpm, HashSet<EPackageState> leftPackages) {
		VersionStringComparator vsc = new VersionStringComparator();
		for (EPackageState state : host.getPackages()) {
			if (state.getVersion().getPkg().getName().equals(irpm.getName())) {
				int comp = vsc.compare(state.getVersion().getVersion(), irpm.getVersion());
				if (comp == 0) {
					break;
				}
				EPackageVersion rpm = this.drpm.find(irpm.getName(), irpm.getVersion());
				if (rpm == null) {
					rpm = new EPackageVersion();
					rpm.setPkg(state.getVersion().getPkg());
					rpm.setVersion(irpm.getVersion());
					rpm.setDeprecated(true);
					rpm = this.drpm.save(rpm);
				}
				leftPackages.remove(state);
				state.setVersion(rpm);
				return this.dpkgstate.save(state);
			}
		}
		return null;
	}
	
	private boolean asserHostServices(ETemplate template, EHost host) {
		List<EService> services = this.dsvc.findList();
		Set<EService> templateServices = new HashSet<>();
		for (EService s : services) {
			for (EPackageVersion p : template.getPackageVersions()) {
				if (s.getPackages().contains(p.getPkg())) {
					templateServices.add(s);
				}
			}
		}
		Set<EService> missingServices = new HashSet<>(templateServices);
		Set<EServiceState> nonUsedServiceStates = new HashSet<>(host.getServices());
		for (EServiceState state : host.getServices()) {
			for (EService service : templateServices) {
				if (service.getName().equals(state.getService().getName())) {
					missingServices.remove(service);
					for (EServiceState ss : nonUsedServiceStates) {
						if (ss.getService().getId().equals(service.getId())) {
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
		for (EService service : missingServices) {
			EServiceState state = new EServiceState();
			state.setService(service);
			state.setHost(host);
			
			EServiceDefaultState dss = this.ddefss.findByName(service.getName(), template.getName());
			if ((dss != null)) {
				state.setState(dss.getState());
			}
			
			this.dsvcstate.save(state);
			changes = true;
		}
		// clean up old no more used service states
		for (EServiceState ss : nonUsedServiceStates) {
			this.dsvcstate.delete(ss);
		}
		return changes;
	}
	
	@Override
	@Transactional
	public ServiceStatesChanges notifyServiceState(String tname, String hname, ServiceStates serviceState, String huuid) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		ETemplate template = this.dtemplate.findByName(tname);
		RESTAssert.assertNotNull(template);
		EHost host = this.getHost(hname, tname, huuid);
		if (this.asserHostServices(template, host)) {
			host = this.dhost.findByName(hname);
		}
		
		Set<String> toStop = new HashSet<>();
		Set<String> toStart = new HashSet<>();
		Set<String> toRestart = new HashSet<>();
		
		Set<EServiceState> stateList = new HashSet<>(host.getServices());
		// agent sends running services
		for (String sname : serviceState.getRunningServices()) {
			for (EServiceState state : host.getServices()) {
				if (state.getService().getName().equals(sname)) {
					stateList.remove(state);
					switch (state.getState()) {
					case RESTARTING_STARTING:
					case STARTING:
						state.nextState();
						this.dsvcstate.save(state);
						break;
					case STOPPING:
						toStop.add(state.getService().getInitScript());
						break;
					case RESTARTING_STOPPING:
						toRestart.add(state.getService().getInitScript());
						state.nextState();
						this.dsvcstate.save(state);
						break;
					case STOPPED:
						state.nextState();
						toStop.add(state.getService().getInitScript());
						this.dsvcstate.save(state);
						break;
					default:
						break;
					}
				}
			}
		}
		
		// agent sends stopped services
		for (EServiceState state : stateList) {
			
			switch (state.getState()) {
			case STARTING:
				toStart.add(state.getService().getInitScript());
				break;
			case STOPPING:
				state.nextState();
				this.dsvcstate.save(state);
				break;
			case STARTED:
				toStart.add(state.getService().getInitScript());
				state.setState(ServiceState.STARTING);
				this.dsvcstate.save(state);
				break;
			default:
				break;
			
			}
			
		}
		
		HashSet<ConfigFile> configFiles = new HashSet<>();
		for (EFile file : template.getConfigFiles()) {
			configFiles.add(MAConverter.fromModel(file));
		}
		
		if (toStart.isEmpty() && toStop.isEmpty() && toRestart.isEmpty() && (host.getStartedUpdate() != null)) {
			host.setStartedUpdate(null);
			this.dhost.save(host);
		}
		ServiceStatesChanges serviceStatesChanges = new ServiceStatesChanges(toStart, toStop, toRestart);
		serviceStatesChanges.setConfigFiles(configFiles);
		return serviceStatesChanges;
	}
	
	@Override
	@Transactional
	public AgentOptions heartBeat(String tname, String hname, String agentN, String huuid) {
		RESTAssert.assertNotEmpty(hname);
		RESTAssert.assertNotEmpty(tname);
		RESTAssert.assertNotEmpty(agentN);
		RESTAssert.assertNotEmpty(huuid);
		EAgent agent = this.dAgent.findAgentByName(agentN);
		if (agent == null) {
			agent = this.createNewAgent(agentN, null);
		}
		EHost host = this.getHost(hname, tname, huuid);
		if(host == null){
			host = this.createNewHost(hname, this.dtemplate.findByName(tname));
			String uuid = UUID.randomUUID().toString();
			host.setUuid(uuid);
		}

		DateTime now = new DateTime();
		host.setLastSeen(now.getMillis());
		host.setAgent(agent);
		host = this.dhost.save(host);
		
		EAgentOption options = this.dagentoptions.findByTemplate(host.getTemplate());
		if (options == null) {
			options = new EAgentOption();
			options.setTemplate(host.getTemplate());
			options = this.dagentoptions.save(options);
		}
		AgentOptions result = MAConverter.fromModel(options);
		result.setTemplateName(host.getTemplate().getName());
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
			this.dhost.save(host);
		}
		result.setUuid(host.getUuid());
		return result;
	}
	
	private EAgent createNewAgent(String agentName, EAgentAuthToken authToken) {
		EAgent agent = new EAgent();
		agent.setName(agentName);
		if (authToken != null) {
			agent.setToken(authToken);
			agent.setTokenAssociationDate(DateTime.now().getMillis());
		}
		return this.dAgent.save(agent);
	}
	
	private ArrayListMultimap<PackageCommand, PackageVersion> computePackageDiff(Collection<EPackageVersion> nominal, Collection<EPackageVersion> actual) {
		// Determine which package versions need to be erased and which are to be installed.
		TreeSet<EPackageVersion> toInstall = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toInstall.addAll(nominal);
		toInstall.removeAll(actual);
		TreeSet<EPackageVersion> toErase = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		toErase.addAll(actual);
		toErase.removeAll(nominal);
		
		// Resolve the removal of an older version and the installation of a newer one to an update instruction.
		TreeSet<EPackageVersion> toUpdate = new TreeSet<EPackageVersion>(new PackageVersionComparator());
		for (EPackageVersion i : toInstall) {
			EPackageVersion e = toErase.lower(i);
			if ((e != null) && e.getPkg().getName().equals(i.getPkg().getName())) {
				toErase.remove(e);
				toUpdate.add(i);
			}
		}
		toInstall.removeAll(toUpdate);
		
		// get rid of reserved packages on erase
		Set<EPackageVersion> keep = new HashSet<>();
		EServerOptions eServerOptions = this.dserveroptions.get();
		for (String pkg : eServerOptions.getDisallowUninstall()) {
			for (EPackageVersion erase : toErase) {
				if (erase.getPkg().getName().equals(pkg)) {
					keep.add(erase);
					break;
				}
			}
		}
		toErase.removeAll(keep);

		// remove dependencies from erase list
		Set<EPackageVersion> dependencies = new HashSet<>();
		for (EPackageVersion i : nominal){
			for (EDependency d : i.getDependencies()) {
				for(EPackageVersion e : toErase) {
					if(e.getPkg().getName().equals(d.getName())){
						dependencies.add(e);
					}
				}
			}
		}
		toErase.removeAll(dependencies);


		// Convert the lists of package versions to lists of RPM descriptions (RPM name, release, and version).
		ArrayListMultimap<PackageCommand, PackageVersion> result = ArrayListMultimap.create();
		result = this.fillPackageDiff(result, PackageCommand.INSTALL, toInstall);
		result = this.fillPackageDiff(result, PackageCommand.UPDATE, toUpdate);
		result = this.fillPackageDiff(result, PackageCommand.ERASE, toErase);
		return result;
	}
	
	private ArrayListMultimap<PackageCommand, PackageVersion> fillPackageDiff(ArrayListMultimap<PackageCommand, PackageVersion> map, PackageCommand command, Collection<EPackageVersion> packageVersions) {
		for (EPackageVersion pv : packageVersions) {
			String rpmName = pv.getPkg().getName();
			Set<Dependency> dep = new HashSet<>();
			for (EDependency edep : pv.getDependencies()) {
				dep.add(MAConverter.fromModel(edep));
			}
			map.put(command, new PackageVersion(rpmName, pv.getVersion(), dep));
		}
		return map;
	}
	
	@Override
	@Transactional
	public boolean isServerAlive() {
		return true;
	}

	private EHost getHost(String name, String tname, String uuid){
		EHost host = this.dhost.findByUuid(uuid);
		if(name.equals(uuid)) {
			host = this.dhost.findByName(name);
			if(host == null){
				host = this.createNewHost(name, this.dtemplate.findByName(tname));
				String newuuid = UUID.randomUUID().toString();
				host.setUuid(newuuid);
			}

		}
		if(host == null){
			host = this.createNewHost(name, this.dtemplate.findByName(tname));
			String newuuid = UUID.randomUUID().toString();
			host.setUuid(newuuid);
		}
		return host;
	}
}
