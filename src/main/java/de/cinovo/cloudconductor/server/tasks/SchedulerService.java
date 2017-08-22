package de.cinovo.cloudconductor.server.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
public class SchedulerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
	/**
	 * the instance
	 */
	public static SchedulerService instance = new SchedulerService();
	
	private final ScheduledExecutorService ses;
	private final HashMap<String, Runnable> tasks;
	private final HashMap<String, ScheduledFuture<?>> runningTasks;
	
	
	private SchedulerService() {
		this.ses = Executors.newScheduledThreadPool(10);
		this.runningTasks = new HashMap<>();
		this.tasks = new HashMap<>();
	}
	
	/**
	 * @param identifier the task identifier
	 * @param task the task
	 * @param period the period
	 * @param unit the time unit
	 */
	public void register(String identifier, Runnable task, long period, TimeUnit unit) {
		if (!this.tasks.containsKey(identifier)) {
			this.tasks.put(identifier, task);
		}
		if (!this.runningTasks.containsKey(identifier)) {
			this.resetTask(identifier, period, unit);
		}
	}
	
	/**
	 * @param identifier the task identifier
	 * @param task the task
	 */
	public void register(String identifier, Runnable task) {
		if (!this.tasks.containsKey(identifier)) {
			this.tasks.put(identifier, task);
		}
	}
	
	/**
	 * @param task the task
	 * @param delay the delay
	 * @param unit the unit
	 * @return the future
	 */
	public ScheduledFuture<?> executeOnce(Runnable task, long delay, TimeUnit unit) {
		return this.ses.schedule(task, delay, unit);
	}
	
	/**
	 * @param identifier the identifier
	 * @return the future
	 */
	public ScheduledFuture<?> executeOnce(String identifier) {
		return this.ses.schedule(this.tasks.get(identifier), 0, TimeUnit.SECONDS);
	}
	
	/**
	 * @param identifier the identifier
	 * @param period the new period
	 * @param unit the new unit
	 */
	public void resetTask(String identifier, long period, TimeUnit unit) {
		SchedulerService.LOGGER.info("Resetting Task " + identifier);
		if (!this.tasks.containsKey(identifier)) {
			throw new RuntimeException("Unknown task: " + identifier);
		}
		long delay = 0;
		if (this.runningTasks.containsKey(identifier)) {
			ScheduledFuture<?> task = this.runningTasks.get(identifier);
			delay = task.getDelay(unit);
			while (!task.isDone()) {
				task.cancel(false);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// just skip
				}
			}
		}
		this.runningTasks.put(identifier, this.ses.scheduleAtFixedRate(this.tasks.get(identifier), delay, period, unit));
	}
	
	/**
	 * @param identifier the identifier
	 */
	public void stop(String identifier) {
		if (!this.tasks.containsKey(identifier)) {
			throw new RuntimeException("Unknown task: " + identifier);
		}
		if (this.runningTasks.containsKey(identifier)) {
			ScheduledFuture<?> task = this.runningTasks.get(identifier);
			while (!task.isDone()) {
				task.cancel(false);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// just skip
				}
			}
			this.runningTasks.remove(identifier);
		}
	}
	
	/**
	 * shut down
	 */
	public void shutdown() {
		this.ses.shutdown();
	}
}
