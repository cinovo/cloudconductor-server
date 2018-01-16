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
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class RepoIndexTask implements IServerRepoTask {

	private static final Logger logger = LoggerFactory.getLogger(IndexTask.class);

	@Autowired
	private IPackageImport packageImport;

	@Autowired
	private IServerOptionsDAO serverOptionsDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private RepoHandler repoHandler;
	private Set<String> indexer = new HashSet<>();

	@Override
	public void create(EServerOptions settings) {
		List<ERepo> repos = this.repoDAO.findList();
		Long delay = 0L;
		for(ERepo repo : repos) {
			this.create(settings, repo, delay);
			delay += settings.getIndexScanTimerUnit().convert(30, TimeUnit.SECONDS);
		}
	}

	private void create(EServerOptions settings, ERepo repo, Long delay) {
		if(settings == null) {
			settings = this.serverOptionsDAO.get();
		}
		IndexTask indexTask = new IndexTask(this.repoDAO, this.repoHandler, this.packageImport, repo.getId());
		this.indexer.add(repo.getName());
		if(delay == null) {
			SchedulerService.instance.register(repo.getName(), indexTask, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit());
		} else {
			SchedulerService.instance.register(repo.getName(), indexTask, settings.getIndexScanTimer(), settings.getIndexScanTimerUnit(), delay);
		}
	}

	@Override
	public void newRepo(ERepo repo) {
		this.create(null, repo, null);
	}

	@Override
	public void updateRepo(String oldRepoName, ERepo newRepo) {
		if(!this.indexer.contains(oldRepoName)) {
			this.newRepo(newRepo);
			return;
		}
		if(oldRepoName.equals(newRepo.getName())) {
			//nothing to do;
			return;
		}
		this.indexer.remove(oldRepoName);
		SchedulerService.instance.stop(oldRepoName);
		this.newRepo(newRepo);

	}

	@Override
	public void deleteRepo(String repoName) {
		if(!this.indexer.contains(repoName)) {
			return;
		}
		SchedulerService.instance.stop(repoName);
	}

	@Override
	public void update(EServerOptions oldSettings, EServerOptions newSettings) {
		boolean change = false;
		if(oldSettings == null) {
			change = true;
		} else {
			if(oldSettings.getIndexScanTimer() != newSettings.getIndexScanTimer()) {
				change = true;
			}
			if(!oldSettings.getIndexScanTimerUnit().equals(newSettings.getIndexScanTimerUnit())) {
				change = true;
			}
		}
		if(change) {
			Long delay = 0L;
			for(String index : this.indexer) {
				SchedulerService.instance.resetTask(index, newSettings.getIndexScanTimer(), newSettings.getIndexScanTimerUnit(), delay);
				delay += newSettings.getIndexScanTimerUnit().convert(30, TimeUnit.SECONDS);
			}
		}
	}

}
