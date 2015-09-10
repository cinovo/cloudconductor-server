package de.cinovo.cloudconductor.server.web;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.SortTool;
import org.joda.time.DateTime;

import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 * 		
 */
public class CSViewModel extends ViewModel {
	
	/**
	 * modal identifier
	 */
	private boolean isSimpleView = true;
	private boolean isModal = false;
	
	
	/**
	 * @param viewName the view name
	 * @param isModal is the view a modal or not
	 * @param options server options
	 */
	public CSViewModel(String viewName, boolean isModal, EServerOptions options) {
		super(viewName);
		this.isSimpleView = false;
		this.isModal = isModal;
		this.addModel("C2InstanceOptions", options);
		String implementationVersion = this.getClass().getPackage().getImplementationVersion();
		this.addModel("C2InstanceVersion", implementationVersion != null ? implementationVersion : "DEV-SNAPSHOT");
		this.addModel("VIEWNAME", viewName);
		
		this.addModel("dateTool", new DateTool());
		this.addModel("sorterTool", new SortTool());
		this.addModel("NOW", DateTime.now());
	}
	
	/**
	 * @param viewName the view name
	 */
	public CSViewModel(String viewName) {
		super(viewName);
		String implementationVersion = this.getClass().getPackage().getImplementationVersion();
		this.addModel("C2InstanceVersion", implementationVersion != null ? implementationVersion : "DEV-SNAPSHOT");
		this.addModel("dateTool", new DateTool());
		this.addModel("sorterTool", new SortTool());
		this.addModel("NOW", DateTime.now());
	}
	
	@Override
	protected String generateTemplateName() {
		if (this.isModal) {
			return "/webres/pages/" + this.getViewName() + ".vm";
		}
		if (this.isSimpleView) {
			return "/webres/pages/" + this.getViewName() + ".vm";
		}
		return "/webres/index.vm";
	}
	
}
