package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IRepo;
import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.tasks.IServerRepoTaskHandler;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class RepoImpl implements IRepo {
	
	@Autowired
	private IRepoDAO repoDAO;
	
	@Autowired
	private RepoHandler repoHandler;
	@Autowired
	private IServerRepoTaskHandler repoTaskHandler;
	
	
	@Override
	@Transactional
	public Repo[] get() {
		List<ERepo> findList = this.repoDAO.findList();
		Set<Repo> result = new HashSet<>();
		for (ERepo repo : findList) {
			result.add(repo.toApi());
		}
		return result.toArray(new Repo[0]);
	}
	
	@Override
	@Transactional
	public Repo get(String name) {
		RESTAssert.assertNotNull(name);
		ERepo repo = this.repoDAO.findByName(name);
		RESTAssert.assertNotNull(repo, Status.NOT_FOUND);
		return repo.toApi();
	}
	
	@Override
	@Transactional
	public Long newRepo(Repo repo) {
		RESTAssert.assertNotNull(repo);
		RESTAssert.assertNotNull(repo.getName());
		
		ERepo saved = this.repoHandler.createRepo(repo);
		return saved.getId();
	}
	
	@Override
	@Transactional
	public void edit(Repo repo) {
		RESTAssert.assertNotNull(repo);
		RESTAssert.assertNotNull(repo.getId());
		
		this.repoHandler.updateRepo(repo);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotNull(name);
		ERepo g = this.repoDAO.findByName(name);
		RESTAssert.assertNotNull(g, Status.NOT_FOUND);
		this.repoHandler.deleteEntity(g);
	}
	
	@Override
	@Transactional
	public void forceReindex(String repoName) {
		RESTAssert.assertNotNull(repoName);
		ERepo g = this.repoDAO.findByName(repoName);
		RESTAssert.assertNotNull(g);
		RESTAssert.assertNotNull(g.getId());
		this.repoTaskHandler.forceRepoUpdate(g.getId());
	}
}
