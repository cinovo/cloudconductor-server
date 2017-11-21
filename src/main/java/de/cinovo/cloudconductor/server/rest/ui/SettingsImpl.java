package de.cinovo.cloudconductor.server.rest.ui;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.ISettings;
import de.cinovo.cloudconductor.api.model.Settings;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.handler.TemplateHandler;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.tasks.ServerTaskHelper;
import de.cinovo.cloudconductor.server.util.GenericModelApiConverter;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class SettingsImpl implements ISettings {
	
	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	
	@Autowired
	private TemplateHandler templateHandler;
	@Autowired
	private ServerTaskHelper taskHelper;
	
	
	@Override
	@Transactional
	public Settings get() {
		EServerOptions options = this.serverOptionsDAO.get();
		return options.toApi();
	}
	
	@Override
	@Transactional
	public void save(Settings settings) {
		RESTAssert.assertNotNull(settings);
		RESTAssert.assertNotNull(settings.getName());
		
		RESTAssert.assertTrue(settings.getHostCleanUpTimer() > 0);
		RESTAssert.assertTrue(settings.getPageRefreshTimer() > 0);
		RESTAssert.assertTrue(settings.getIndexScanTimer() > 0);
		
		EServerOptions newOptions = GenericModelApiConverter.convert(settings, EServerOptions.class);
		EServerOptions oldOptions = this.serverOptionsDAO.get();
		newOptions.setId(oldOptions.getId());
		newOptions = this.serverOptionsDAO.save(newOptions);
		
		if (!newOptions.isAllowautoupdate()) {
			this.templateHandler.disableAutoUpdate();
		}
		
		this.taskHelper.updateTasks(oldOptions);
	}
}
