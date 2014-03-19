package de.cinovo.cloudconductor.server.web2;

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
	
	/**
	 * modal identifier
	 */
	public static final String MODAL_IDENTIFIER = "_MODAL_";
	private static final SimpleDateFormat NOW_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
	
	
	/**
	 * @param viewName the view name
	 * @param isModal is the view a modal or not
	 * @param options server options
	 */
	public CSViewModel(String viewName, boolean isModal, EServerOptions options) {
		super(viewName);
		this.addModel("C2InstanceOptions", options);
		String implementationVersion = this.getClass().getPackage().getImplementationVersion();
		this.addModel("C2InstanceVersion", implementationVersion != null ? implementationVersion : "(unknown)");
		if (isModal) {
			this.addModel("VIEWNAME", CSViewModel.MODAL_IDENTIFIER + viewName);
		} else {
			this.addModel("VIEWNAME", viewName);
		}
		
		this.addModel("listTool", new ListTool());
		this.addModel("sorterTool", new SortTool());
		this.addModel("NOW", CSViewModel.NOW_TIME_FORMAT.format(new Date()));
		
		// if ((additionalTitle == null) || additionalTitle.isEmpty()) {
		// this.addModel("windowtitle", "CloudConductor");
		// } else {
		// this.addModel("windowtitle", "CloudConductor " + additionalTitle);
		// }
		// this.addModel("subheading", subheading);
		//
	}
}
