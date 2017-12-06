package de.cinovo.cloudconductor.server.tasks;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EServerOptions;

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
		for (IServerTasks task : this.tasks) {
			task.create(options);
		}
	}
	
	/**
	 * @param oldOptions the old options
	 * @param newOptions the new options
	 */
	public void updateTasks(EServerOptions oldOptions, EServerOptions newOptions) {
		for (IServerTasks task : this.tasks) {
			task.update(oldOptions, newOptions);
		}
	}
	
}
