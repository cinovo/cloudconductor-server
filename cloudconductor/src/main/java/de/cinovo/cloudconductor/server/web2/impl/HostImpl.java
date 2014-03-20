package de.cinovo.cloudconductor.server.web2.impl;

import de.cinovo.cloudconductor.server.web2.helper.AWebPage;
import de.cinovo.cloudconductor.server.web2.interfaces.IHost;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;

public class HostImpl extends AWebPage implements IHost {
	
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
	public ViewModel view() {
		ViewModel view = this.createView();
		return view;
	}
	
}
