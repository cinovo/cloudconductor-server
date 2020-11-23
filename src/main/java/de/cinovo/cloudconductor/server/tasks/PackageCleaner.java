package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.dao.IFileDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackage;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2020 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class PackageCleaner implements IServerTasks {
	
	private final Logger logger = LoggerFactory.getLogger(PackageCleaner.class);
	
	@Value("${packageCleaner.active:true}")
	private String active;
	
	@Autowired
	private IPackageDAO packageDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IFileDAO fileDAO;
	
	@Override
	@Transactional
	public void run() {
		List<EPackage> emptyPackages = new ArrayList<>();
		for (EPackage pkg : this.packageDAO.findList()) {
			List<EPackageVersion> versions = this.packageVersionDAO.findByPackage(pkg.getId());
			if (versions.isEmpty()) {
				emptyPackages.add(pkg);
				continue;
			}
			
			int deleteCount = 0;
			for (EPackageVersion version : versions) {
				if (!version.getRepos().isEmpty()) {
					continue;
				}
				if (this.templateDAO.countUsingPackageVersion(version) <= 0) {
					this.logger.info("Removing unprovided and unused package version: {}-{}", version.getPkgName(), version.getVersion());
					this.packageVersionDAO.deleteById(version.getId());
					deleteCount++;
				}
			}
			if (deleteCount == versions.size()) {
				emptyPackages.add(pkg);
			}
			
		}
		
		for (EPackage emptyPackage : emptyPackages) {
			if (this.fileDAO.countByPackage(emptyPackage) <= 0) {
				this.packageDAO.deleteById(emptyPackage.getId());
			}
		}
	}
	
	@Override
	public String getTaskIdentifier() {
		return "PACKAGE_CLEAN_UP_TASK";
	}
	
	@Override
	public Integer getTimer() {
		return 1;
	}
	
	@Override
	public TimeUnit getTimerUnit() {
		return TimeUnit.DAYS;
	}
	
	@Override
	public Integer getDelay() {
		return 0;
	}
	
	@Override
	public TaskStateChange checkStateChange(EServerOptions oldSettings, EServerOptions newSettings) {
		if (this.active.equalsIgnoreCase("true")) {
			return TaskStateChange.START;
		}
		return TaskStateChange.NONE;
	}
}

