package de.cinovo.cloudconductor.server.web.helper;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.SortTool;

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
	
	private static final SimpleDateFormat FETCH_DATE_FORMAT = new SimpleDateFormat("'on' MMM d, yyyy 'at' hh:mm:ss a", Locale.US);
	
	
	/**
	 * @param viewName the name of the view
	 * @param options server options
	 */
	public CSViewModel(String viewName, EServerOptions options) {
		this(viewName, null, null, options);
	}
	
	/**
	 * @param viewName the name of the view
	 * @param additionalTitle additional titeles for the view
	 * @param options server options
	 */
	public CSViewModel(String viewName, String additionalTitle, EServerOptions options) {
		this(viewName, additionalTitle, null, options);
	}
	
	/**
	 * @param viewName the view name
	 * @param subheading a subheading
	 * @param additionalTitle the additional window title
	 * @param options server options
	 */
	public CSViewModel(String viewName, String additionalTitle, String subheading, EServerOptions options) {
		super(viewName);
		this.addModel("serverOptions", options);
		this.addModel("modelName", viewName);
		this.addModel("fetchDate", CSViewModel.FETCH_DATE_FORMAT.format(new Date()));
		this.addModel("listTool", new ListTool());
		this.addModel("sorterTool", new SortTool());
		if ((additionalTitle == null) || additionalTitle.isEmpty()) {
			this.addModel("windowtitle", "CloudConductor");
		} else {
			this.addModel("windowtitle", "CloudConductor " + additionalTitle);
		}
		this.addModel("subheading", subheading);
		
		String implementationVersion = this.getClass().getPackage().getImplementationVersion();
		this.addModel("csversion", implementationVersion != null ? implementationVersion : "(unknown)");
	}
	
}
