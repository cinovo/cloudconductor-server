package de.cinovo.cloudconductor.server.ws.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.ws.AParamWSAdapter;
import de.cinovo.cloudconductor.server.ws.AParamWSHandler;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@Service
public class TemplateDetailWSHandler extends AParamWSHandler<AParamWSAdapter<Template>, Template> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDetailWSHandler.class);
	
	
	@Override
	public void addSocketAdapter(String templateName, AParamWSAdapter<Template> a) {
		super.addSocketAdapter(templateName, a);
		TemplateDetailWSHandler.LOGGER.info("Added WS for details of template {}", templateName);
	}
	
	@Override
	public boolean removeSocketAdapter(String templateName, AParamWSAdapter<Template> a) {
		boolean success = super.removeSocketAdapter(templateName, a);
		TemplateDetailWSHandler.LOGGER.info("Removed WS for details of template {}", templateName);
		return success;
	}
	
	@Override
	public void broadcastChange(String templateName, WSChangeEvent<Template> event) {
		super.broadcastChange(templateName, event);
		TemplateDetailWSHandler.LOGGER.info("Broadcasted change for template {}", templateName);
	}
}
