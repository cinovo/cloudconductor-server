package de.cinovo.cloudconductor.server.rest.ui;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IStats;
import de.cinovo.cloudconductor.api.model.Statistics;
import de.cinovo.cloudconductor.server.dao.IHostDAO;
import de.cinovo.cloudconductor.server.dao.IPackageDAO;
import de.cinovo.cloudconductor.server.dao.IServiceDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@JaxRsComponent
public class StatsImpl implements IStats {
	
	@Autowired
	private IHostDAO hostDao;
	@Autowired
	private ITemplateDAO templateDao;
	@Autowired
	private IPackageDAO packageDao;
	@Autowired
	private IServiceDAO serviceDao;

	@Override
	public Statistics get() {
		Statistics stats = new Statistics();
		stats.setNumberOfHosts(this.hostDao.count());
		stats.setNumberOfTemplates(this.templateDao.count());
		stats.setNumberOfPackages(this.packageDao.count());
		stats.setNumberOfServices(this.serviceDao.count());
		return stats;
	}

}
