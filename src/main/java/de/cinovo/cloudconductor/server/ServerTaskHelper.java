package de.cinovo.cloudconductor.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.cinovo.cloudconductor.api.lib.helper.SchedulerService;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.IndexTask;
import de.cinovo.cloudconductor.server.util.CleanUpTask;
import de.cinovo.cloudconductor.server.util.ICCProperties;
import de.cinovo.cloudconductor.server.util.IServerTasks;

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
	private CleanUpTask cleanupTask;
	@Autowired
	private IndexTask indexTask;


	/**
	 *
	 */
	public void init() {
		EServerOptions options = this.optionsDao.get();
		this.createCleanUpTask(options);
		this.createIndexTask(options);
	}

	private void createCleanUpTask(EServerOptions options) {
		SchedulerService.instance.register(IServerTasks.CLEAN_UP, this.cleanupTask, options.getHostCleanUpTimer(), options.getHostCleanUpTimerUnit());
	}
	
	private void createIndexTask(EServerOptions options) {
		if (System.getProperty(ICCProperties.REPO_SCAN, "false").equals("true")) {
			SchedulerService.instance.register(IServerTasks.INDEXER, this.indexTask, options.getIndexScanTimer(), options.getIndexScanTimerUnit());
		}
	}

	/**
	 * update the tasks
	 */
	public void updateTasks() {
		EServerOptions options = this.optionsDao.get();
		this.updateCleanUpTask(null, options);
		this.updateIndexTask(null, options);
	}
	
	/**
	 * @param oldOptions the old options
	 */
	public void updateTasks(EServerOptions oldOptions) {
		EServerOptions options = this.optionsDao.get();
		this.updateCleanUpTask(oldOptions, options);
		this.updateIndexTask(oldOptions, options);
	}
	
	private void updateCleanUpTask(EServerOptions oldOptions, EServerOptions options) {
		boolean change = false;
		if (oldOptions == null) {
			change = true;
		} else {
			if (oldOptions.getHostCleanUpTimer() != options.getHostCleanUpTimer()) {
				change = true;
			}
			if (!oldOptions.getHostCleanUpTimerUnit().equals(options.getHostCleanUpTimerUnit())) {
				change = true;
			}
		}
		if (change) {
			SchedulerService.instance.resetTask(IServerTasks.CLEAN_UP, options.getHostCleanUpTimer(), options.getHostCleanUpTimerUnit());
		}
	}

	private void updateIndexTask(EServerOptions oldOptions, EServerOptions options) {
		boolean change = false;
		if (oldOptions == null) {
			change = true;
		} else {
			if (oldOptions.getIndexScanTimer() != options.getIndexScanTimer()) {
				change = true;
			}
			if (!oldOptions.getIndexScanTimerUnit().equals(options.getIndexScanTimerUnit())) {
				change = true;
			}
		}
		if (change) {
			SchedulerService.instance.resetTask(IServerTasks.INDEXER, options.getIndexScanTimer(), options.getIndexScanTimerUnit());
		}
	}

}
