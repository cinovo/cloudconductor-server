package de.cinovo.cloudconductor.server.web2.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.cinovo.cloudconductor.api.model.INamed;
import de.cinovo.cloudconductor.server.dao.IAuditLogDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EAuditLog;
import de.cinovo.cloudconductor.server.model.tools.AuditCategory;
import de.cinovo.cloudconductor.server.web.helper.FormErrorException;
import de.cinovo.cloudconductor.server.web.helper.FormErrorExceptionHander;
import de.cinovo.cloudconductor.server.web2.CSViewModel;
import de.cinovo.cloudconductor.server.web2.comparators.INamedComparator;
import de.cinovo.cloudconductor.server.web2.interfaces.IContextAware;
import de.cinovo.cloudconductor.server.web2.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.ViewModel;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public abstract class AWebPage implements IContextAware {
	
	@Autowired
	protected NavbarRegistry navRegistry;
	
	@Autowired
	private IAuditLogDAO dAudit;
	@Autowired
	protected IServerOptionsDAO dServerOptions;
	
	private LinkedHashMap<String, String> breadcrumbs = new LinkedHashMap<>();
	private Set<String> sidebar = Sets.newTreeSet();
	
	protected MessageContext mc;
	
	
	protected abstract String getTemplateFolder();
	
	@PostConstruct
	protected abstract void init();
	
	@Override
	public void setMessageContext(MessageContext context) {
		this.mc = context;
	}
	
	protected abstract String getNavElementName();
	
	protected SidebarType getSidebarType() {
		return SidebarType.SIMPLE;
	}
	
	protected String getUser() {
		return this.mc.getSecurityContext().getUserPrincipal().getName();
	}
	
	protected AuditCategory getAuditCategory() {
		return AuditCategory.UNDEFINED;
	}
	
	protected ViewModel createView() {
		CSViewModel view = new CSViewModel(this.getTemplateFolder() + "/view", false, this.dServerOptions.get());
		view.addModel("BREDCRUMBS", this.breadcrumbs.entrySet());
		view.addModel("SIDEBAR", this.sidebar);
		view.addModel("SIDEBARTYPE", this.getSidebarType());
		view.addModel("NAVELEMENT", this.navRegistry);
		view.addModel("CURRENTNAVELEMENT", this.getNavElementName());
		return view;
	}
	
	protected ViewModel createModal(String modalName) {
		CSViewModel modal = new CSViewModel(CSViewModel.MODAL_IDENTIFIER + this.getTemplateFolder() + "/" + modalName, true, this.dServerOptions.get());
		if (this.hasError()) {
			FormErrorException error = this.pollError();
			modal.addModel("ERROR", error);
		}
		return modal;
	}
	
	protected void addBreadCrumb(String link, String name) {
		if (this.breadcrumbs.isEmpty()) {
			this.breadcrumbs.put("Home", IWebPath.WEBROOT);
		}
		this.breadcrumbs.put(name, link);
	}
	
	protected void addSidebarElement(String element) {
		this.sidebar.add(element);
	}
	
	protected void addSidebarElement(Collection<String> elements) {
		this.sidebar.addAll(elements);
	}
	
	protected <K extends Comparable<K>, V> List<Map.Entry<K, V>> sortMap(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			
			@Override
			public int compare(Map.Entry<K, V> a, Map.Entry<K, V> b) {
				return a.getKey().compareTo(b.getKey());
			}
		});
		return entries;
	}
	
	protected <E extends INamed> void sortNamedList(List<E> list) {
		Collections.sort(list, new INamedComparator());
	}
	
	protected FormErrorException createError(String message) {
		String path = this.mc.getHttpServletRequest().getPathInfo();
		path = path.substring(IWebPath.WEBROOT.length(), path.length());
		return new FormErrorException(path, message);
	}
	
	protected Boolean hasError() {
		if (this.mc.getHttpServletRequest().getParameter(FormErrorExceptionHander.REQUEST_ERROR_PARAM) != null) {
			return this.mc.getHttpServletRequest().getParameter(FormErrorExceptionHander.REQUEST_ERROR_PARAM).equals("true");
		}
		return false;
	}
	
	protected FormErrorException pollError() {
		FormErrorException result = (FormErrorException) this.mc.getHttpServletRequest().getSession(true).getAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
		this.mc.getHttpServletRequest().getSession().removeAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
		return result;
	}
	
	protected FormErrorException checkForEmpty(String variable, String errorMessage, FormErrorException error, String formElement) {
		if ((variable == null) || variable.isEmpty()) {
			FormErrorException anError = error;
			if (error == null) {
				anError = this.createError(errorMessage);
			}
			anError.addElementError(formElement, true);
			return anError;
		}
		return error;
	}
	
	/**
	 * @param entry the entry
	 */
	protected void audit(String entry) {
		EAuditLog log = new EAuditLog();
		log.setTimestamp(DateTime.now().getMillis());
		log.setUsername(this.getUser());
		log.setCategory(this.getAuditCategory());
		log.setEntry(entry);
		this.dAudit.save(log);
	}
	
}
