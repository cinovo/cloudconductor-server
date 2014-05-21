package de.cinovo.cloudconductor.server.web;

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

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
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
	
	static {
		try {
			// Use ClasspathLoader
			Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
			Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			// Use UTF-8
			Velocity.setProperty("input.encoding", "UTF-8");
			Velocity.setProperty("output.encoding", "UTF-8");
			// Use log4j
			Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getCanonicalName());
			Velocity.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
			Velocity.init();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static String evaluateVM(final String name, final Map<String, Object> variables) {
		try {
			/* lets make a Context and put data into it */
			final VelocityContext context = new VelocityContext();
			
			final Set<Entry<String, Object>> entrySet = variables.entrySet();
			for (final Entry<String, Object> entry : entrySet) {
				context.put(entry.getKey(), entry.getValue());
			}
			
			final Template template = Velocity.getTemplate(name);
			final StringWriter w = new StringWriter();
			template.merge(context, w);
			return w.toString();
		} catch (final Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	
	/**
	 * modal identifier
	 */
	public static final String MODAL_IDENTIFIER = "_MODAL_";
	
	
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
		
		this.addModel("dateTool", new DateTool());
		this.addModel("sorterTool", new SortTool());
		this.addModel("NOW", DateTime.now());
	}
	
	protected String generateTemplateName() {
		if (this.getViewName().startsWith(CSViewModel.MODAL_IDENTIFIER)) {
			String view = this.getViewName().substring(CSViewModel.MODAL_IDENTIFIER.length(), this.getViewName().length());
			return "/web/pages/" + view + ".vm";
		}
		return "/web/index.vm";
	}
	
	/**
	 * @return the rendered view
	 */
	public RenderedView render() {
		RenderedView view = new RenderedView();
		String evaluateVM = CSViewModel.evaluateVM(this.generateTemplateName(), this.getModel());
		view.setContent(evaluateVM);
		return view;
	}
}
