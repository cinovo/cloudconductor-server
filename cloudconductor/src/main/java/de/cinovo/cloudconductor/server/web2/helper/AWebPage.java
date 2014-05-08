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
import de.cinovo.cloudconductor.server.web2.impl.IndexImpl;
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
	
	private LinkedHashMap<String, String> topActions = new LinkedHashMap<>();
	private LinkedHashMap<String, String> breadcrumbs = new LinkedHashMap<>();
	private List<ViewFilter> filters = new ArrayList<>();
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
		return this.createView("view");
	}
	
	protected String getCurrentFilter() {
		if (this.filters.isEmpty()) {
			return null;
		}
		String f = this.mc.getUriInfo().getQueryParameters().getFirst("filter");
		if (f != null) {
			for (ViewFilter vf : this.filters) {
				if (vf.getId().equals(f)) {
					return vf.getName();
				}
			}
		}
		for (ViewFilter vf : this.filters) {
			if (vf.isDefault()) {
				return vf.getName();
			}
		}
		return this.filters.iterator().next().getName();
	}
	
	protected void addFilter(String id, String name, boolean isDefault) {
		this.filters.add(new ViewFilter(id, name, isDefault));
		Collections.sort(this.filters);
	}
	
	protected ViewModel createView(String viewname) {
		CSViewModel view = new CSViewModel(this.getTemplateFolder() + "/" + viewname, false, this.dServerOptions.get());
		view.addModel("BREDCRUMBS", this.breadcrumbs.entrySet());
		view.addModel("SIDEBAR", this.sidebar);
		view.addModel("SIDEBARTYPE", this.getSidebarType());
		view.addModel("NAVELEMENT", this.navRegistry);
		view.addModel("CURRENTNAVELEMENT", this.getNavElementName());
		view.addModel("TOPACTIONS", this.topActions.entrySet());
		view.addModel("FILTERS", this.filters);
		view.addModel("CURRENTFILTER", this.getCurrentFilter());
		view.addModel(IndexImpl.AUTOREFRESH, this.mc.getHttpServletRequest().getSession().getAttribute(IndexImpl.AUTOREFRESH));
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
	
	protected void addTopAction(String link, String name) {
		this.topActions.put(name, link);
	}
	
	protected void removeSidebarElement(String element) {
		this.sidebar.remove(element);
	}
	
	protected void addSidebarElement(String element) {
		this.sidebar.add(element);
	}
	
	protected void addSidebarElement(Collection<String> elements) {
		this.sidebar.addAll(elements);
	}
	
	protected void addSidebarElements(Collection<? extends INamed> elements) {
		for (INamed n : elements) {
			this.sidebar.add(n.getName());
		}
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
	
	protected FormErrorException assertNotEmpty(String variable, FormErrorException error, String formElement) {
		return this.checkForEmpty(variable, null, error, formElement);
	}
	
	protected FormErrorException checkForEmpty(String variable, String errorMessage, FormErrorException error, String formElement) {
		String eMsg = "Please fill in all the information.";
		if ((errorMessage != null) && !errorMessage.isEmpty()) {
			eMsg = errorMessage;
		}
		if ((variable == null) || variable.isEmpty()) {
			FormErrorException anError = error;
			if (error == null) {
				anError = this.createError(eMsg);
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
	
	protected String auditFormat(String[] str) {
		StringBuilder b = new StringBuilder();
		for (String s : str) {
			b.append(s);
			b.append(",");
		}
		return b.deleteCharAt(b.length() - 1).toString();
	}
}
