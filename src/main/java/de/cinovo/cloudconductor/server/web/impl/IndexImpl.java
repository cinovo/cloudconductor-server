package de.cinovo.cloudconductor.server.web.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import de.cinovo.cloudconductor.server.dao.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.ServiceState;
import de.cinovo.cloudconductor.server.model.EHost;
import de.cinovo.cloudconductor.server.model.EPackageState;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServiceState;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.interfaces.IIndex;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.RenderedUI;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
public class IndexImpl extends AWebPage implements IIndex {
	
	/** */
	public static final String AUTOREFRESH = "AUTOREFRESH";
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected IHostDAO dHost;
	@Autowired
	protected IServiceStateDAO dSvcState;
	@Autowired
	protected IPackageDAO dPkg;
	@Autowired
	protected IServiceDAO dSvc;
	@Autowired
	protected IFileDAO dFile;
	@Autowired
	protected ISSHKeyDAO dSSH;
	@Autowired
	protected IDirectoryDAO ddir;
	
	
	@Override
	public void init() {
		this.navRegistry.registerMainMenu(this.getNavElementName(), IWebPath.DEFAULTVIEW);
	}
	
	@Override
	protected String getNavElementName() {
		return "Home";
	}
	
	@Override
	public InputStream getCSS(String css) {
		return this.getClass().getResourceAsStream("/webres/css/" + css);
	}
	
	@Override
	public InputStream getBSCSS(String css) {
		return this.getClass().getResourceAsStream("/webres/bootstrap/css/" + css);
	}
	
	@Override
	public InputStream getImage(String img) {
		return this.getClass().getResourceAsStream("/webres/images/" + img);
	}
	
	@Override
	public InputStream getJS(String js) {
		return this.getClass().getResourceAsStream("/webres/js/" + js);
	}
	
	@Override
	public InputStream getBSJS(String js) {
		return this.getClass().getResourceAsStream("/webres/bootstrap/js/" + js);
	}
	
	@Override
	public InputStream getBSFonts(String font) {
		return this.getClass().getResourceAsStream("/webres/bootstrap/fonts/" + font);
	}
	
	@Override
	protected String getTemplateFolder() {
		return "start";
	}
	
	@Override
	@Transactional
	public RenderedUI view() {
		List<EHost> hostList = this.dHost.findList();
		List<ETemplate> templateList = this.dTemplate.findList();
		CSViewModel view = this.createView();
		view.addModel("LINKLIST", this.dLinks.findList());
		view.addModel("TEMPLATELIST", this.getTemplateList(templateList));
		view.addModel("HOSTLIST", this.getHostWatch(hostList));
		view.addModel("SERCVICELIST", this.getServiceWatch());
		view.addModel("HOSTCOUNT", hostList.size());
		view.addModel("PACKAGECOUNT", this.dPkg.count());
		view.addModel("SERVICECOUNT", this.dSvc.count());
		view.addModel("FILECOUNT", this.dFile.count());
		view.addModel("SSHKEYCOUNT", this.dSSH.count());
		view.addModel("DIRCOUNT", this.ddir.count());
		return view.render();
	}
	
	private List<Entry<String, String>> getTemplateList(List<ETemplate> templateList) {
		HashMap<String, String> result = new HashMap<>();
		for (ETemplate t : templateList) {
			result.put(t.getName(), String.valueOf(t.getHosts().size()));
		}
		return this.sortMap(result);
	}
	
	private List<Entry<String, String[]>> getHostWatch(List<EHost> hostList) {
		HashMap<String, String[]> result = new HashMap<>();
		for (EHost h : hostList) {
			DateTime now = new DateTime();
			String[] error = new String[2];
			if (now.minusMinutes(15).getMillis() > h.getLastSeen()) {
				error[0] = "offline";
			}
			if (!this.hostInSync(h)) {
				error[1] = "not synced";
			}
			if (error.length < 1) {
				result.put(h.getName(), error);
			}
		}
		return this.sortMap(result);
	}
	
	private boolean hostInSync(EHost h) {
		List<EPackageVersion> trpms = h.getTemplate().getPackageVersions();
		if ((h.getPackages() == null) || h.getPackages().isEmpty()) {
			return false;
		}
		for (EPackageState hpkg : h.getPackages()) {
			if (!trpms.contains(hpkg.getVersion())) {
				return false;
			}
		}
		
		for (EPackageVersion v : trpms) {
			boolean found = false;
			for (EPackageState hpkg : h.getPackages()) {
				if (v.equals(hpkg.getVersion())) {
					found = true;
					continue;
				}
			}
			if (!found) {
				return false;
			}
		}
		
		return true;
	}
	
	private List<Entry<String, ServiceState>> getServiceWatch() {
		HashMap<String, ServiceState> result = new HashMap<>();
		for (EServiceState ss : this.dSvcState.findList()) {
			switch (ss.getState()) {
			case RESTARTING_STARTING:
			case RESTARTING_STOPPING:
			case STARTING:
				if (result.get(ss.getService().getName()) == ServiceState.STOPPED) {
					result.put(ss.getService().getName(), ServiceState.STARTING);
				}
				break;
				
			case STOPPED:
			case STOPPING:
				if (result.get(ss.getService().getName()) == null) {
					result.put(ss.getService().getName(), ServiceState.STOPPED);
				}
				break;
			default:
				break;
			}
		}
		return this.sortMap(result);
	}
	
	@Override
	public AjaxAnswer toggleAutoRefresh() {
		final HttpSession session = this.mc.getHttpServletRequest().getSession();
		Boolean val = (Boolean) session.getAttribute(IndexImpl.AUTOREFRESH);
		if (val == null) {
			session.setAttribute(IndexImpl.AUTOREFRESH, true);
		} else {
			session.setAttribute(IndexImpl.AUTOREFRESH, !val.booleanValue());
		}
		String path = this.mc.getHttpServletRequest().getPathInfo();
		path = path.substring(IWebPath.WEBROOT.length(), path.length());
		return new AjaxAnswer(path);
	}
}
