package de.cinovo.cloudconductor.server.web.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;

import de.cinovo.cloudconductor.api.lib.helper.SchedulerService;
import de.cinovo.cloudconductor.server.ServerStarter;
import de.cinovo.cloudconductor.server.util.FormErrorException;
import de.cinovo.cloudconductor.server.util.FormErrorExceptionHander;
import de.cinovo.cloudconductor.server.util.ICCProperties;
import de.cinovo.cloudconductor.server.util.RestartTask;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.RenderedView;
import de.cinovo.cloudconductor.server.web.interfaces.IContextAware;
import de.cinovo.cloudconductor.server.web.interfaces.IInstall;

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
	public RenderedView view() {
		CSViewModel view = new CSViewModel("install/install");
		if (this.hasError()) {
			FormErrorException error = (FormErrorException) this.mc.getHttpServletRequest().getSession(true).getAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
			this.mc.getHttpServletRequest().getSession().removeAttribute(FormErrorExceptionHander.FORM_ERROR_DATA);
			view.addModel("ERROR", error);
		}
		return view.render();
	}

	private boolean fieldCheck(String formField, String propField, MultivaluedMap<String, String> form, Properties props, FormErrorException e) {
		if (form.get(formField) == null) {
			e.addFormParam(formField, true);
			return false;
		} else if ((form.get(formField).get(0) == null) || form.get(formField).get(0).isEmpty()) {
			e.addFormParam(formField, form.get(formField).get(0), true);
			return false;
		}
		props.put(propField, form.get(formField).get(0));
		return true;
	}
	
	@Override
	public Response save(MultivaluedMap<String, String> form) throws FormErrorException {
		Properties props = new Properties();
		FormErrorException error = new FormErrorException("", "Please fill in all the information.");
		if (this.fieldCheck(InstallImpl.DB_TYPE_FORM, ICCProperties.DB_TYPE, form, props, error)) {
			if (form.get(InstallImpl.DB_TYPE_FORM).get(0).equals("-1")) {
				error.addFormParam(InstallImpl.DB_TYPE_FORM, form.get(InstallImpl.DB_TYPE_FORM).get(0), true);
			}
		}
		this.fieldCheck(InstallImpl.DB_HOST_FORM, ICCProperties.DB_HOST, form, props, error);
		this.fieldCheck(InstallImpl.DB_PORT_FORM, ICCProperties.DB_PORT, form, props, error);
		this.fieldCheck(InstallImpl.DB_USER_FORM, ICCProperties.DB_USER, form, props, error);
		this.fieldCheck(InstallImpl.DB_PW_FORM, ICCProperties.DB_PW, form, props, error);
		this.fieldCheck(InstallImpl.DB_NAME_FORM, ICCProperties.DB_NAME, form, props, error);

		this.fieldCheck(InstallImpl.CC_PORT_FORM, ICCProperties.CC_PORT, form, props, error);
		this.fieldCheck(InstallImpl.CC_NAME_FORM, ICCProperties.CC_NAME, form, props, error);
		this.fieldCheck(InstallImpl.CC_USER_FORM, ICCProperties.CC_USER, form, props, error);
		this.fieldCheck(InstallImpl.CC_PW_FORM, ICCProperties.CC_PW, form, props, error);

		if (this.fieldCheck(InstallImpl.REPO_SCAN_FORM, ICCProperties.REPO_SCAN, form, props, error)) {
			if (form.get(InstallImpl.REPO_SCAN_FORM).get(0) == "true") {
				this.fieldCheck(InstallImpl.REPO_INDEXER_FORM, ICCProperties.REPO_INDEXER, form, props, error);
			}
		}

		if (this.fieldCheck(InstallImpl.REPO_PROVIDER_FORM, ICCProperties.REPO_PROVIDER, form, props, error)) {
			if (form.get(InstallImpl.REPO_PROVIDER_FORM).get(0).equals("-1")) {
				error.addFormParam(InstallImpl.REPO_PROVIDER_FORM, form.get(InstallImpl.REPO_PROVIDER_FORM).get(0), true);
			}
			switch (form.get(InstallImpl.REPO_PROVIDER_FORM).get(0)) {
			case "FileProvider":
				this.fieldCheck(InstallImpl.REPO_BASEDIR_FORM, ICCProperties.REPO_BASEDIR, form, props, error);
				break;
			case "HTTPProvider":
				this.fieldCheck(InstallImpl.REPO_BASEURL_FORM, ICCProperties.REPO_BASEURL, form, props, error);
				break;
			case "AWSS3Provider":
				this.fieldCheck(InstallImpl.REPO_AWS_BUCKET_FORM, ICCProperties.REPO_AWS_BUCKET, form, props, error);
				this.fieldCheck(InstallImpl.REPO_AWS_ACCESS_KEY_FORM, ICCProperties.REPO_AWS_ACCESS_KEY, form, props, error);
				this.fieldCheck(InstallImpl.REPO_AWS_SECRET_KEY_FORM, ICCProperties.REPO_AWS_SECRET_KEY, form, props, error);
				break;
			default:
				break;
			}
		}
		if (error.containsElementErrors()) {
			throw error;
		}

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
	
	@Override
	public RenderedView progressView() {
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
