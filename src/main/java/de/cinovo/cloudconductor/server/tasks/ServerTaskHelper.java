package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Component
public class ServerTaskHelper {
	
	@Autowired
	private IServerOptionsDAO optionsDao;
	
	@Autowired
	private Set<IServerTasks> tasks;
	
	/**
	 *
	 */
	public void init() {
		EServerOptions options = this.optionsDao.get();
		for(IServerTasks task : tasks) {
			task.create(options);
		}
	}

	/**
	 * @param oldOptions the old options
	 */
	public void updateTasks(EServerOptions oldOptions) {
		EServerOptions options = this.optionsDao.get();
		for(IServerTasks task : tasks) {
			task.update(oldOptions, options);
		}
	}

}
