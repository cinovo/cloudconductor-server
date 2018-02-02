package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component
@EnableScheduling
public class ServerTaskHelper implements SchedulingConfigurer, IServerRepoTaskHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IServerOptionsDAO optionsDAO;
	@Autowired
	private Set<IServerTasks> tasks;
	@Autowired
	private TaskScheduler scheduler;

	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private IPackageImport packageImport;
	@Autowired
	private RepoHandler repoHandler;

	private Map<String, ScheduledFuture<?>> running = new HashMap<>();

	/**
	 * on class init
	 */
	@PostConstruct
	public void init() {
		EServerOptions settings = this.optionsDAO.get();
		this.initRepoIndexerTasks(settings);
	}

	/**
	 * on destory, stop all tasks
	 */
	@PreDestroy
	public void shutdown() {
		this.tasks.forEach(this::stopTask);
	}

	/**
	 * @param oldOptions the old options
	 * @param newOptions the new options
	 */
	public void updateTasks(EServerOptions oldOptions, EServerOptions newOptions) {
		for(IServerTasks task : this.tasks) {
			switch(task.checkStateChange(oldOptions, newOptions)) {
				case NONE:
					break;
				case START:
					if(!this.running.containsKey(task.getTaskIdentifier())) {
						this.startTask(task);
					}
					break;
				case RESTART:
					this.startTask(task);
					break;
				case STOP:
					this.stopTask(task);
					break;
			}
		}
	}

	private void stopTask(IServerTasks task) {
		if(this.running.containsKey(task.getTaskIdentifier())) {
			ScheduledFuture<?> future = this.running.get(task.getTaskIdentifier());
			future.cancel(false);
			this.running.remove(task.getTaskIdentifier());
		}
	}

	private void startTask(IServerTasks task) {
		this.stopTask(task);
		if(task.getTimer() != null && task.getTimerUnit() != null) {
			ScheduledFuture<?> future = this.scheduler.schedule(task, this.createTrigger(task));
			this.running.put(task.getTaskIdentifier(), future);
		} else {
			this.logger.error("Failed to start task {}: no delay was set.", task.getTaskIdentifier());
		}

	}

	private Trigger createTrigger(IServerTasks task) {
		return (triggerContext) -> {
			Date lastCompletion = triggerContext.lastCompletionTime();
			DateTime nextExecution;
			if(lastCompletion == null) {
				nextExecution = DateTime.now().plusMillis(task.getDelay());
			} else {
				nextExecution = new DateTime(lastCompletion).plusMillis((int) task.getTimerUnit().toMillis(task.getTimer()));
			}
			return nextExecution.toDate();
		};
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		EServerOptions eServerOptions = this.optionsDAO.get();
		this.updateTasks(null, eServerOptions);
	}


	private void initRepoIndexerTasks(EServerOptions settings) {
		if(settings == null) {
			settings = this.optionsDAO.get();
		}
		//create repo index tasks
		List<ERepo> list = this.repoDAO.findList();
		long delaySpan = TimeUnit.MILLISECONDS.convert(settings.getIndexScanTimer(), settings.getIndexScanTimerUnit());
		delaySpan = delaySpan / list.size() + 1;
		int repoCount = 0;
		for(ERepo repo : list) {
			this.createRepoIndexTask(settings, (int) (repoCount * delaySpan), repo.getId());
			repoCount++;
		}
	}

	private void createRepoIndexTask(EServerOptions settings, int repoDelay, Long repoId) {
		if(repoId == null) {
			return;
		}
		boolean taskExists = this.tasks.stream().map(IServerTasks::getTaskIdentifier).anyMatch((IndexTask.TASK_ID_PREFIX + repoId)::equals);
		if(!taskExists) {
			IndexTask indexTask = new IndexTask(this.repoDAO, this.repoHandler, this.packageImport, repoId, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit(), repoDelay);
			this.tasks.add(indexTask);
		}
	}

	@Override
	public void newRepo(ERepo repo) {
		EServerOptions settings = this.optionsDAO.get();
		this.createRepoIndexTask(settings, 0, repo.getId());
	}

	@Override
	public void deleteRepo(long repoId) {
		Set<IServerTasks> tasksToDelete = this.tasks.stream().filter((t) -> t.getTaskIdentifier().equals(IndexTask.TASK_ID_PREFIX + repoId)).collect(Collectors.toSet());
		tasksToDelete.forEach(this::stopTask);
		tasksToDelete.forEach(this.tasks::remove);
	}

	@Override
	public void forceRepoUpdate(long repoId) {
		Set<IServerTasks> tasksToDelete = this.tasks.stream().filter((t) -> t.getTaskIdentifier().equals(IndexTask.TASK_ID_PREFIX + repoId)).collect(Collectors.toSet());
		tasksToDelete.forEach(IServerTasks::run);
	}
}
