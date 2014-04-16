package de.cinovo.cloudconductor.server.web2.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.web2.comparators.StateComparator;
import de.cinovo.cloudconductor.server.web2.comparators.StringMapComparator;
import de.cinovo.cloudconductor.server.web2.comparators.VersionStringComparator;
import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.helper.AjaxRedirect;
import de.cinovo.cloudconductor.server.web2.interfaces.IHost;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class HostImpl extends AWebPage implements IHost {
	
	private DateTimeFormatter germanFmt = DateTimeFormat.forPattern("HH:mm:ss - dd.MM.yyyy");
	@Autowired
	protected IHostDAO dHost;
	
	
	@Override
	protected String getTemplateFolder() {
		return "hosts";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerMainMenu(this.getNavElementName(), IHost.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IHost.ROOT, this.getNavElementName());
	}
	
	@Override
	protected String getNavElementName() {
		return "Hosts";
	}
	
	@Override
	@Transactional
	public ViewModel view() {
		List<EHost> eHosts = this.dHost.findList();
		Map<String, String[]> reporttimes = new HashMap<>();
		Map<String, List<?>> thDiff = new HashMap<>();
		for (EHost h : eHosts) {
			this.addSidebarElement(h.getName());
			Collections.sort(h.getServices(), new StateComparator());
			h.getTemplate().getName(); // this line is caused by lazy loading
			DateTime last = new DateTime(h.getLastSeen());
			String diff = String.valueOf(Minutes.minutesBetween(last, new DateTime()).getMinutes());
			reporttimes.put(h.getName(), new String[] {diff, this.germanFmt.print(last)});
			thDiff.put(h.getName(), this.createHostTemplateDiff(h));
		}
		this.sortNamedList(eHosts);
		ViewModel view = this.createView();
		view.addModel("HOSTS", eHosts);
		view.addModel("DIFFERENCES", thDiff);
		return view;
	}
	
	@Override
	@Transactional
	public ViewModel view(String hname) {
		EHost eHosts = this.dHost.findByName(hname);
		Collections.sort(eHosts.getServices(), new StateComparator());
		eHosts.getTemplate().getName(); // this line is caused by lazy loading
		ViewModel modal = this.createModal("mSingleView");
		modal.addModel("host", eHosts);
		modal.addModel("DIFFERENCES", this.createHostTemplateDiff(eHosts));
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect changeServiceStates(String hname, String[] start, String[] stop, String[] restart) {
		RESTAssert.assertNotEmpty(hname);
		if ((start.length < 1) && (stop.length < 1) && (restart.length < 1)) {
			new AjaxRedirect(IWebPath.WEBROOT + IHost.ROOT);
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
		return new AjaxRedirect(IWebPath.WEBROOT + IHost.ROOT);
	}
	
	@Override
	@Transactional
	public ViewModel deleteHostView(String hname) {
		RESTAssert.assertNotEmpty(hname);
		EHost host = this.dHost.findByName(hname);
		RESTAssert.assertNotNull(host);
		ViewModel modal = this.createModal("deleteHost");
		modal.addModel("host", host);
		return modal;
	}
	
	@Override
	@Transactional
	public AjaxRedirect deleteHost(String hname) {
		RESTAssert.assertNotEmpty(hname);
		EHost host = this.dHost.findByName(hname);
		if ((host != null)) {
			this.dHost.delete(host);
		}
		this.audit("Manually deleted host " + hname);
		return new AjaxRedirect(IWebPath.WEBROOT + IHost.ROOT);
	}
	
	private List<Map<String, Object>> createHostTemplateDiff(EHost h) {
		List<EPackageVersion> trpms = h.getTemplate().getPackageVersions();
		Set<EPackageVersion> missing = new HashSet<>(trpms);
		VersionStringComparator versionComp = new VersionStringComparator();
		StringMapComparator mapComp = new StringMapComparator("name");
		List<Map<String, Object>> notices = new ArrayList<>();
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
					Map<String, Object> wrongVersion = new HashMap<>();
					wrongVersion.put("name", ipkg.getVersion().getPkg().getName());
					wrongVersion.put("installed", ipkg.getVersion().getVersion());
					wrongVersion.put("template", trpm.getVersion());
					if (versionComp.compare(ipkg.getVersion().getVersion(), trpm.getVersion()) < 0) {
						wrongVersion.put("state", "updating");
					} else {
						wrongVersion.put("state", "downgrading");
					}
					found = true;
					notices.add(wrongVersion);
					missing.remove(trpm);
					continue;
				}
			}
			// rpm is not within template
			if (!found) {
				Map<String, Object> wrongVersion = new HashMap<>();
				wrongVersion.put("name", ipkg.getVersion().getPkg().getName());
				wrongVersion.put("installed", ipkg.getVersion().getVersion());
				wrongVersion.put("template", "");
				wrongVersion.put("state", "uninstalling");
				notices.add(wrongVersion);
			}
		}
		
		for (EPackageVersion trpm : missing) {
			Map<String, Object> wrongVersion = new HashMap<>();
			wrongVersion.put("name", trpm.getPkg().getName());
			wrongVersion.put("installed", "");
			wrongVersion.put("template", trpm.getVersion());
			wrongVersion.put("state", "installing");
			notices.add(wrongVersion);
		}
		Collections.sort(notices, mapComp);
		return notices;
	}
}