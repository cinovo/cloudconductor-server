package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IServerOptionsDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.cinovo.cloudconductor.server.repo.importer.IPackageImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	@Value("${indexTaskType:all}")
	private String indexTaskType;

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

	private Map<String, AbstractTrigger> running = new HashMap<>();


	/**
	 * on class init
	 */
	@PostConstruct
	public void init() {
		EServerOptions settings = this.optionsDAO.get();
		this.initRepoIndexerTasks(settings);
	}

	/**
	 * on destroy, stop all tasks
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
		String taskId = task.getTaskIdentifier();
		if(this.running.containsKey(taskId)) {
			AbstractTrigger trigger = this.running.get(taskId);
			trigger.stop();
			this.running.remove(taskId);
		}
	}

	private void startTask(IServerTasks task) {
		this.stopTask(task);

		if((task.getTimer() != null) && (task.getTimerUnit() != null)) {
			AbstractTrigger aTrigger = new RateTrigger(task);
			ScheduledFuture<?> future = this.scheduler.schedule(task, aTrigger);
			aTrigger.setFuture(future);

			this.running.put(task.getTaskIdentifier(), aTrigger);
		} else {
			this.logger.error("Failed to start task {}: no unit was set.", task.getTaskIdentifier());
		}
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

		if(this.indexTaskType.equals("single")) {
			// create repo index tasks
			List<ERepo> list = this.repoDAO.findList();
			if((list == null) || list.isEmpty()) {
				return;
			}
			long delaySpan = TimeUnit.MILLISECONDS.convert(settings.getIndexScanTimer(), settings.getIndexScanTimerUnit());
			delaySpan = (delaySpan / list.size()) + 1;
			int repoCount = 0;
			for(ERepo repo : list) {
				if((repo.getLastIndex() == null) || repo.getLastIndex().equals(0L)) {
					this.createRepoIndexTask(settings, 0, repo.getId());
				} else {
					this.createRepoIndexTask(settings, (int) (repoCount * delaySpan), repo.getId());
				}
				repoCount++;
			}

		} else {
			this.tasks.add(new AllIndexTask(this.repoDAO, this.repoHandler, this.packageImport, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit(), 0));
		}
	}

	private SingleIndexTask createRepoIndexTask(EServerOptions settings, int repoDelay, Long repoId) {
		if(repoId == null) {
			return null;
		}

		Optional<SingleIndexTask> existingTask = this.tasks.stream() //
				.filter(t -> (t instanceof SingleIndexTask) && t.getTaskIdentifier().equals(SingleIndexTask.TASK_ID_PREFIX + repoId)) //
				.map(SingleIndexTask.class::cast) //
				.findAny();

		if(!existingTask.isPresent()) {
			SingleIndexTask singleIndexTask = new SingleIndexTask(this.repoDAO, this.repoHandler, this.packageImport, repoId, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit(), repoDelay);
			this.tasks.add(singleIndexTask);
			return singleIndexTask;
		}

		return existingTask.get();
	}

	@Override
	public void newRepo(ERepo repo) {
		if(!repo.getRepoMirrors().isEmpty() && this.indexTaskType.equals("single")) {
			EServerOptions settings = this.optionsDAO.get();
			SingleIndexTask newSingleIndexTask = this.createRepoIndexTask(settings, 0, repo.getId());
			this.startTask(newSingleIndexTask);
		}
	}

	@Override
	public void deleteRepo(long repoId) {
		Set<IServerTasks> tasksToDelete = this.tasks.stream().filter((t) -> t.getTaskIdentifier().equals(SingleIndexTask.TASK_ID_PREFIX + repoId)).collect(Collectors.toSet());
		tasksToDelete.forEach(this::stopTask);
		tasksToDelete.forEach(this.tasks::remove);
	}

	@Override
	public void forceRepoUpdate(long repoId) {
		if(this.indexTaskType.equals("single")) {
			Set<IServerTasks> tasksToForce = this.tasks.stream().filter((t) -> t.getTaskIdentifier().equals(SingleIndexTask.TASK_ID_PREFIX + repoId)).collect(Collectors.toSet());
			for(IServerTasks task : tasksToForce) {
				if(task instanceof SingleIndexTask) {
					((SingleIndexTask) task).forceRun();
				}
			}
		} else {
			SingleIndexTask singleIndexTask = new SingleIndexTask(this.repoDAO, this.repoHandler, this.packageImport, repoId, 0, TimeUnit.MINUTES, 0);
			singleIndexTask.forceRun();
		}
	}

}
