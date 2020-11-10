package de.cinovo.cloudconductor.server.ws.template;

import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent;
import de.cinovo.cloudconductor.server.websockets.model.WSChangeEvent.ChangeType;
import de.cinovo.cloudconductor.server.ws.AParamWSAdapter;
import de.cinovo.cloudconductor.server.ws.AParamWSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
public class TemplateDetailWSHandler extends AParamWSHandler<AParamWSAdapter<Template>, Template> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateDetailWSHandler.class);
	@Autowired
	private IHostDAO hostDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	
	@Override
	public void addSocketAdapter(String templateName, AParamWSAdapter<Template> a) {
		super.addSocketAdapter(templateName, a);
		TemplateDetailWSHandler.LOGGER.debug("Added WS for details of template {}", templateName);
	}
	
	@Override
	public boolean removeSocketAdapter(String templateName, AParamWSAdapter<Template> a) {
		boolean success = super.removeSocketAdapter(templateName, a);
		TemplateDetailWSHandler.LOGGER.debug("Removed WS for details of template {}", templateName);
		return success;
	}
	
	/**
	 * @param template template
	 * @param type     type
	 */
	public void broadcastChange(ETemplate template, ChangeType type) {
		WSChangeEvent<Template> twsChangeEvent = new WSChangeEvent<>(type, template.toApi(this.hostDAO, this.repoDAO, this.packageVersionDAO));
		super.broadcastChange(template.getName(), twsChangeEvent);
		TemplateDetailWSHandler.LOGGER.debug("Broadcasted change for host '{}'", template.getName());
	}
	
}
