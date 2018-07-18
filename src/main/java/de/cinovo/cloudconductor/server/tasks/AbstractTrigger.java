package de.cinovo.cloudconductor.server.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AbstractTrigger implements Trigger {
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private boolean isCancelled = false;
	private ScheduledFuture<?> future;
	private IServerTasks task;
	
	
	/**
	 * @param task the task for the new trigger
	 */
	public AbstractTrigger(IServerTasks task) {
		this.task = task;
	}
	
	/**
	 * @return next execution time as String
	 */
	public abstract String getNextExecutionAsString();
	
	/**
	 * @param future the future
	 */
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}
	
	/**
	 * cancel the trigger
	 */
	public void stop() {
		if (this.future != null) {
			this.future.cancel(false);
		}
		this.isCancelled = true;
	}
	
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		if (this.isCancelled) {
			return null;
		}
		return this.getNextExectionTime(triggerContext);
	}
	
	protected abstract Date getNextExectionTime(TriggerContext triggerContext);
	
	/**
	 * @return the task
	 */
	public IServerTasks getTask() {
		return this.task;
	}
	
}
