package de.cinovo.cloudconductor.server.web.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;

import de.cinovo.cloudconductor.api.lib.helper.SchedulerService;
import de.cinovo.cloudconductor.server.ServerStarter;
import de.cinovo.cloudconductor.server.util.ICCProperties;
import de.cinovo.cloudconductor.server.util.RestartTask;
import de.cinovo.cloudconductor.server.util.exception.FormErrorException;
import de.cinovo.cloudconductor.server.util.exception.FormErrorExceptionHander;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.FormValidator;
import de.cinovo.cloudconductor.server.web.interfaces.IContextAware;
import de.cinovo.cloudconductor.server.web.interfaces.IInstall;
import de.taimos.cxf_renderer.model.RenderedUI;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *		
 */
public class InstallImpl implements IInstall, IContextAware {
	
	private static final String DB_TYPE_FORM = "db-type";
	
	private static final String DB_HOST_FORM = "db-host";
	private static final String DB_PORT_FORM = "db-port";
	private static final String DB_USER_FORM = "db-user";
	private static final String DB_PW_FORM = "db-pw";
	private static final String DB_NAME_FORM = "db-name";
	
	private static final String CC_PORT_FORM = "cc-port";
	private static final String CC_NAME_FORM = "cc-name";
	private static final String CC_USER_FORM = "cc-user";
	private static final String CC_PW_FORM = "cc-pw";
	
	private static final String REPO_SCAN_FORM = "repo-scan";
	private static final String REPO_INDEXER_FORM = "repo-indexer";
	
	private static final String REPO_PROVIDER_FORM = "repo-provider";
	private static final String REPO_BASEDIR_FORM = "repo-basedir";
	private static final String REPO_BASEURL_FORM = "repo-baseurl";
	private static final String REPO_AWS_BUCKET_FORM = "repo-bucket";
	private static final String REPO_AWS_ACCESS_KEY_FORM = "aws-accessKeyId";
	private static final String REPO_AWS_SECRET_KEY_FORM = "aws-secretKey";
	
	
	@Override
	public RenderedUI view() {
		CSViewModel view = new CSViewModel("install/install");
		if (this.hasError()) {
			FormErrorException error = (FormErrorException) this.mc.getHttpServletRequest().getSession(true).getAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
			this.mc.getHttpServletRequest().getSession().removeAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
			view.addModel("ERROR", error);
		}
		return view.render();
	}
	
	@Override
	public Response save(MultivaluedMap<String, String> form) throws FormErrorException {
		
		FormValidator validator = FormValidator.create("", form);
		
		validator.notEmpty(InstallImpl.DB_TYPE_FORM);
		if (validator.fieldCheck(InstallImpl.DB_TYPE_FORM)) {
			validator.notEquals(InstallImpl.DB_TYPE_FORM, "-1");
		}
		validator.notEmpty(InstallImpl.DB_HOST_FORM).notEmpty(InstallImpl.DB_PORT_FORM).notEmpty(InstallImpl.DB_USER_FORM).notEmpty(InstallImpl.DB_PW_FORM).notEmpty(InstallImpl.DB_NAME_FORM);
		
		validator.notEmpty(InstallImpl.CC_PORT_FORM).notEmpty(InstallImpl.CC_NAME_FORM).notEmpty(InstallImpl.CC_USER_FORM).notEmpty(InstallImpl.CC_PW_FORM);
		
		validator.notEmpty(InstallImpl.REPO_SCAN_FORM);
		if (validator.fieldCheck(InstallImpl.REPO_SCAN_FORM)) {
			if (form.get(InstallImpl.REPO_SCAN_FORM).get(0) == "true") {
				validator.notEmpty(InstallImpl.REPO_INDEXER_FORM);
			}
		}
		
		validator.notEmpty(InstallImpl.REPO_PROVIDER_FORM);
		if (validator.fieldCheck(InstallImpl.REPO_PROVIDER_FORM)) {
			validator.notEquals(InstallImpl.REPO_PROVIDER_FORM, "-1");
			switch (form.get(InstallImpl.REPO_PROVIDER_FORM).get(0)) {
			case "FileProvider":
				validator.notEmpty(InstallImpl.REPO_BASEDIR_FORM);
				break;
			case "HTTPProvider":
				validator.notEmpty(InstallImpl.REPO_BASEURL_FORM);
				break;
			case "AWSS3Provider":
				validator.notEmpty(InstallImpl.REPO_AWS_BUCKET_FORM);
				validator.notEmpty(InstallImpl.REPO_AWS_ACCESS_KEY_FORM);
				validator.notEmpty(InstallImpl.REPO_AWS_SECRET_KEY_FORM);
				break;
			default:
				break;
			}
		}
		validator.validate();
		
		Properties props = this.generateProps(form);
		
		File f = new File(ServerStarter.CLOUDCONDUCTOR_PROPERTIES);
		try (OutputStream out = new FileOutputStream(f);) {
			props.store(out, "");
		} catch (IOException e) {
			throw new FormErrorException("/", "Couldn't write the config file! Please check the folder rights.");
		}
		SchedulerService.instance.executeOnce(new RestartTask(), 10, TimeUnit.SECONDS);
		try {
			return Response.seeOther(new URI("/finish/")).build();
		} catch (URISyntaxException e) {
			return Response.serverError().build();
		}
	}
	
	private Properties generateProps(MultivaluedMap<String, String> form) {
		Properties props = new Properties();
		for (Entry<String, List<String>> entry : form.entrySet()) {
			String key = null;
			switch (entry.getKey()) {
			case DB_TYPE_FORM:
				key = ICCProperties.DB_TYPE;
				break;
			case DB_HOST_FORM:
				key = ICCProperties.DB_HOST;
				break;
			case DB_PORT_FORM:
				key = ICCProperties.DB_PORT;
				break;
			case DB_USER_FORM:
				key = ICCProperties.DB_USER;
				break;
			case DB_PW_FORM:
				key = ICCProperties.DB_PW;
				break;
			case DB_NAME_FORM:
				key = ICCProperties.DB_NAME;
				break;
				
			case CC_PORT_FORM:
				key = ICCProperties.CC_PORT;
				break;
			case CC_NAME_FORM:
				key = ICCProperties.CC_NAME;
				break;
			case CC_USER_FORM:
				key = ICCProperties.CC_USER;
				break;
			case CC_PW_FORM:
				key = ICCProperties.CC_PW;
				break;
				
			case REPO_SCAN_FORM:
				key = ICCProperties.REPO_SCAN;
				break;
			case REPO_INDEXER_FORM:
				key = ICCProperties.REPO_INDEXER;
				break;
				
			case REPO_PROVIDER_FORM:
				key = ICCProperties.REPO_PROVIDER;
				break;
			case REPO_BASEDIR_FORM:
				key = ICCProperties.REPO_BASEDIR;
				break;
			case REPO_BASEURL_FORM:
				key = ICCProperties.REPO_BASEURL;
				break;
			case REPO_AWS_BUCKET_FORM:
				key = ICCProperties.REPO_AWS_BUCKET;
				break;
			case REPO_AWS_ACCESS_KEY_FORM:
				key = ICCProperties.REPO_AWS_ACCESS_KEY;
				break;
			case REPO_AWS_SECRET_KEY_FORM:
				key = ICCProperties.REPO_AWS_SECRET_KEY;
				break;
			}
			
			if ((key != null) && (entry.getValue().get(0) != null) && !entry.getValue().get(0).isEmpty()) {
				props.setProperty(key, entry.getValue().get(0));
			}
		}
		return props;
	}
	
	@Override
	public RenderedUI progressView() {
		CSViewModel view = new CSViewModel("install/progress");
		return view.render();
	}
	
	@Override
	public InputStream getCSS(String css) {
		return this.getClass().getResourceAsStream("/web/css/" + css);
	}
	
	@Override
	public InputStream getBSCSS(String css) {
		return this.getClass().getResourceAsStream("/web/bootstrap/css/" + css);
	}
	
	@Override
	public InputStream getImage(String img) {
		return this.getClass().getResourceAsStream("/web/images/" + img);
	}
	
	@Override
	public InputStream getJS(String js) {
		return this.getClass().getResourceAsStream("/web/js/" + js);
	}
	
	@Override
	public InputStream getBSJS(String js) {
		return this.getClass().getResourceAsStream("/web/bootstrap/js/" + js);
	}
	
	@Override
	public InputStream getBSFonts(String font) {
		return this.getClass().getResourceAsStream("/web/bootstrap/fonts/" + font);
	}
	
	
	protected MessageContext mc;
	
	
	@Override
	public void setMessageContext(MessageContext context) {
		this.mc = context;
	}
	
	protected Boolean hasError() {
		if (this.mc.getHttpServletRequest().getParameter(FormErrorExceptionHander.REQUEST_ERROR_PARAM) != null) {
			return this.mc.getHttpServletRequest().getParameter(FormErrorExceptionHander.REQUEST_ERROR_PARAM).equals("true");
		}
		return false;
	}
}
