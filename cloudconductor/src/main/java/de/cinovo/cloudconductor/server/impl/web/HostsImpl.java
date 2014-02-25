package de.cinovo.cloudconductor.server.impl.web;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.web.interfaces.IHost;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class HostsImpl extends AbstractWebImpl implements IHost {
	
	/** max time a host may be offline until it's delete from the list */
	public static final int MAX_TIMEOUT_HOST = 30;
	/** max time a host may be offline until it's marked as fishy */
	public static final int WARNING_TIMEOUT_HOST = 10;
	
	private DateTimeFormatter germanFmt = DateTimeFormat.forPattern("HH:mm:ss - dd.MM.yyyy");
	
	private static final String AUTOREFRESH = "AUTOREFRESH";
	
	
	@Override
	protected String getTemplateFolder() {
		return "hosts";
	}
	
	@Override
	protected String getAdditionalTitle() {
		return "Hosts";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IHost.ROOT;
	}
	
	@Override
	protected AuditCategory getAuditCategory() {
		return AuditCategory.HOST;
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EHost> hosts = this.dHost.findList();
		List<Map<String, Object>> result = new ArrayList<>();
		
		for (EHost h : hosts) {
			Map<String, Object> host = new HashMap<>();
			host.put("name", h.getName());
			host.put("template", h.getTemplate().getName());
			host.put("description", h.getDescription());
			
			DateTime now = new DateTime();
			DateTime dt = new DateTime(h.getLastSeen());
			int diff = Minutes.minutesBetween(dt, now).getMinutes();
			host.put("lastseen", diff + " Minutes ago  (" + this.germanFmt.print(dt) + ")");
			
			if (diff > HostsImpl.MAX_TIMEOUT_HOST) {
				host.put("hoststate", "error");
			} else if (diff > HostsImpl.WARNING_TIMEOUT_HOST) {
				host.put("hoststate", "warning");
			} else {
				host.put("hoststate", "ok");
			}
			
			Set<EPackageVersion> trpms = h.getTemplate().getRPMs();
			Set<EPackageVersion> missing = new HashSet<>(trpms);
			
			List<Map<String, Object>> pkgErrors = new ArrayList<>();
			for (EPackageState ipkg : h.getPackages()) {
				if (trpms.contains(ipkg.getVersion())) {
					// version is ok
					missing.remove(ipkg.getVersion());
					continue;
				}
				boolean found = false;
				for (EPackageVersion trpm : trpms) {
					// version is not same as in template
					if (trpm.getPkg().getName().equals(ipkg.getVersion().getPkg().getName())) {
						Map<String, Object> rpmError = new HashMap<>();
						rpmError.put("name", ipkg.getVersion().getPkg().getName());
						rpmError.put("versionInstalled", ipkg.getVersion().getVersion());
						rpmError.put("versionTemplate", trpm.getVersion());
						rpmError.put("state", "wrong version");
						found = true;
						pkgErrors.add(rpmError);
						missing.remove(trpm);
						continue;
					}
				}
				// rpm is not within template
				if (!found) {
					Map<String, Object> rpmError = new HashMap<>();
					rpmError.put("name", ipkg.getVersion().getPkg().getName());
					rpmError.put("versionInstalled", ipkg.getVersion().getVersion());
					rpmError.put("versionTemplate", "");
					rpmError.put("state", "package not in template");
					pkgErrors.add(rpmError);
				}
			}
			
			for (EPackageVersion trpm : missing) {
				Map<String, Object> rpmError = new HashMap<>();
				rpmError.put("name", trpm.getPkg().getName());
				rpmError.put("versionInstalled", "");
				rpmError.put("versionTemplate", trpm.getVersion());
				rpmError.put("state", "package not installed");
				pkgErrors.add(rpmError);
			}
			Collections.sort(pkgErrors, this.nameMapComp);
			host.put("pkgerrors", pkgErrors);
			
			List<Map<String, Object>> services = new ArrayList<>();
			for (EServiceState sstate : h.getServices()) {
				Map<String, Object> service = new HashMap<>();
				service.put("name", sstate.getService().getName());
				service.put("state", sstate.getState());
				services.add(service);
			}
			Collections.sort(services, this.nameMapComp);
			host.put("services", services);
			result.add(host);
		}
		Collections.sort(result, this.nameMapComp);
		final ViewModel vm = this.createView();
		vm.addModel("hosts", result);
		vm.addModel("currentPage", IHost.ROOT);
		Boolean autorefresh = (Boolean) this.mc.getHttpServletRequest().getSession().getAttribute(HostsImpl.AUTOREFRESH);
		vm.addModel(HostsImpl.AUTOREFRESH, autorefresh);
		return vm;
	}
	
	@Override
	@Transactional
	public Response handleServices(String hname, String[] start, String[] stop, String[] restart) {
		RESTAssert.assertNotEmpty(hname);
		if ((start.length < 1) && (stop.length < 1) && (restart.length < 1)) {
			return this.redirect(null, hname);
		}
		EHost host = this.dHost.findByName(hname);
		for (String service : start) {
			for (EServiceState eservice : host.getServices()) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.STARTING);
					break;
				}
			}
		}
		for (String service : stop) {
			for (EServiceState eservice : host.getServices()) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.STOPPING);
					break;
				}
			}
		}
		for (String service : restart) {
			for (EServiceState eservice : host.getServices()) {
				if (eservice.getService().getName().equals(service)) {
					eservice.setState(ServiceState.RESTARTING);
					break;
				}
			}
		}
		this.dHost.save(host);
		return this.redirect(null, hname);
	}
	
	@Override
	@Transactional
	public ViewModel viewDelete(String hname) {
		RESTAssert.assertNotEmpty(hname);
		String msg = "Do you really want to remove the host <b>" + hname + "</b>?";
		String header = "Remove host " + hname;
		String back = "#" + hname;
		return this.createDeleteView(header, msg, back, hname);
	}
	
	@Override
	@Transactional
	public Object delete(String hname) {
		RESTAssert.assertNotEmpty(hname);
		EHost host = this.dHost.findByName(hname);
		if ((host != null)) {
			this.dHost.delete(host);
		}
		this.log("Manually deleted host " + hname);
		return this.redirect();
	}
	
	@Override
	@Transactional
	public Response handleAutorefresh() {
		final HttpSession session = this.mc.getHttpServletRequest().getSession();
		Boolean val = (Boolean) session.getAttribute(HostsImpl.AUTOREFRESH);
		if (val == null) {
			session.setAttribute(HostsImpl.AUTOREFRESH, true);
		} else {
			session.setAttribute(HostsImpl.AUTOREFRESH, !val.booleanValue());
		}
		return this.redirect();
	}
}
