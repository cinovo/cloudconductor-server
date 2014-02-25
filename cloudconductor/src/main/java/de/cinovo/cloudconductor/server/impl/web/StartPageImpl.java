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

import java.io.InputStream;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.web.helper.CSViewModel;
import de.cinovo.cloudconductor.server.web.interfaces.IStartPage;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class StartPageImpl extends AbstractWebImpl implements IStartPage {
	
	@Override
	protected String getTemplateFolder() {
		return "";
	}
	
	@Override
	protected String getRelativeRootPath() {
		return IWebPath.DEFAULTVIEW;
	}
	
	/**
	 * @return the main page with links
	 */
	@Override
	@Transactional
	public ViewModel view() {
		// Fill template and return.
		final ViewModel vm = new CSViewModel(this.getTemplateFolder() + "view", this.getAdditionalTitle(), this.dServerOptions.get());
		vm.addModel("links", this.dLinks.findList());
		return vm;
	}
	
	/**
	 * Returns the CSS style used in the web pages.
	 * 
	 * @return the CSS style used in the web pages
	 */
	@Override
	public InputStream getStyle() {
		return this.getClass().getResourceAsStream("/templates/style.css");
	}
	
	/**
	 * Returns the CSS style used in the web pages.
	 * 
	 * @param img the img name
	 * 
	 * @return the CSS style used in the web pages
	 */
	@Override
	public InputStream getImage(String img) {
		return this.getClass().getResourceAsStream("/templates/images/" + img);
	}
	
}
