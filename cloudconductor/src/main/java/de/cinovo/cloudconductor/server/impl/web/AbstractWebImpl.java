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

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.dao.IAuditLogDAO;
import de.cinovo.cloudconductor.server.dao.IConfigValueDAO;
import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IFileDataDAO;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ISSHKeyDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDefaultStateDAO;
import de.cinovo.cloudconductor.server.dao.IServiceStateDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.model.EAuditLog;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.util.StringMapComparator;
import de.cinovo.cloudconductor.server.web.helper.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.FormErrorExceptionHander;
import de.cinovo.cloudconductor.server.web.interfaces.IContextAware;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public abstract class AbstractWebImpl implements IContextAware {
	
	@Autowired
	private IAuditLogDAO dAudit;
	
	@Autowired
	protected ITemplateDAO dTemplate;
	@Autowired
	protected IHostDAO dHost;
	@Autowired
	protected IPackageDAO dPkg;
	@Autowired
	protected IServiceStateDAO dSvcState;
	@Autowired
	protected IPackageServerDAO dPackageServer;
	@Autowired
	protected IServiceDefaultStateDAO dSvcDefState;
	@Autowired
	protected IServiceDAO dSvc;
	@Autowired
	protected IFileDAO dFile;
	@Autowired
	protected IFileDataDAO dFileData;
	@Autowired
	protected IConfigValueDAO dConfig;
	@Autowired
	protected IServerOptionsDAO dServerOptions;
	@Autowired
	protected IAdditionalLinksDAO dLinks;
	@Autowired
	protected IPackageVersionDAO dVersion;
	@Autowired
	protected ISSHKeyDAO dSSH;
	
	protected StringMapComparator nameMapComp = new StringMapComparator("name");
	protected MessageContext mc;
	
	
	protected abstract String getTemplateFolder();
	
	protected abstract String getRelativeRootPath();
	
	@Override
	public void setMessageContext(MessageContext context) {
		this.mc = context;
	}
	
	protected String getUser() {
		return this.mc.getSecurityContext().getUserPrincipal().getName();
	}
	
	protected AuditCategory getAuditCategory() {
		return AuditCategory.UNDEFINED;
	}
	
	protected String getAdditionalTitle() {
		return "";
	}
	
	protected ViewModel createView(String template) {
		return new CSViewModel(this.getTemplateFolder() + "/" + template, this.getAdditionalTitle(), this.dServerOptions.get());
	}
	
	protected ViewModel createView() {
		return new CSViewModel(this.getTemplateFolder() + "/view", this.getAdditionalTitle(), this.dServerOptions.get());
	}
	
	protected ViewModel createDeleteView(String header, String descr, String back, String... pathParts) {
		return this.createDeleteView(header, descr, back, false, pathParts);
	}
	
	protected ViewModel createDeleteView(String header, String descr, String back, boolean removeFlag, String... pathParts) {
		ViewModel vm = new CSViewModel("includes/confirmDelete", this.getAdditionalTitle(), this.dServerOptions.get());
		vm.addModel("title", header);
		vm.addModel("backButton", IWebPath.WEBROOT + this.getRelativeRootPath() + back);
		vm.addModel("headline", header);
		vm.addModel("descr", descr);
		StringBuilder builder = new StringBuilder();
		builder.append(IWebPath.WEBROOT);
		builder.append(this.getRelativeRootPath());
		for (String part : pathParts) {
			if (!part.startsWith("/")) {
				builder.append("/");
			}
			builder.append(part);
		}
		if (removeFlag) {
			builder.append(IWebPath.ACTION_REMOVE);
		} else {
			builder.append(IWebPath.ACTION_DELETE);
		}
		vm.addModel("actionTarget", builder.toString());
		return vm;
	}
	
	protected Response redirect() {
		return this.redirect(null, null);
	}
	
	protected Response redirect(String target) {
		return this.redirect(target, null);
	}
	
	protected Response redirect(String target, String anchor) {
		StringBuilder uri = new StringBuilder();
		uri.append(this.getRelativeRootPath());
		if ((target != null) && !target.isEmpty()) {
			if (!target.startsWith("/")) {
				uri.append("/");
			}
			uri.append(target);
		}
		if ((anchor != null) && !anchor.isEmpty()) {
			if (!anchor.startsWith("#")) {
				uri.append("#");
			}
			uri.append(anchor);
		}
		try {
			return Response.seeOther(new URI(uri.toString())).build();
		} catch (URISyntaxException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	protected MultivaluedMap<String, String> getErrorFields() {
		return (MultivaluedMap<String, String>) this.mc.getHttpServletRequest().getSession().getAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
	}
	
	/**
	 * @param entry the entry
	 */
	protected void log(String entry) {
		EAuditLog log = new EAuditLog();
		log.setTimestamp(DateTime.now().getMillis());
		log.setUsername(this.getUser());
		log.setCategory(this.getAuditCategory());
		log.setEntry(entry);
		this.dAudit.save(log);
	}
	
	protected String arrayToString(String[] str) {
		StringBuilder b = new StringBuilder();
		for (String s : str) {
			b.append(s);
			b.append(",");
		}
		return b.deleteCharAt(b.length() - 1).toString();
	}
	
}
