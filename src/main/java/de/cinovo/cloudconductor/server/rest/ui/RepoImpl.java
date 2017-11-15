package de.cinovo.cloudconductor.server.rest.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IRepo;
import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

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
	
	
	@Override
	@Transactional
	public Repo[] get() {
		List<ERepo> findList = this.repoDAO.findList();
		Set<Repo> result = new HashSet<>();
		for (ERepo repo : findList) {
			result.add(repo.toApi());
		}
		return result.toArray(new Repo[result.size()]);
	}
	
	@Override
	@Transactional
	public Repo get(String name) {
		RESTAssert.assertNotNull(name);
		ERepo repo = this.repoDAO.findByName(name);
		RESTAssert.assertNotNull(repo);
		return repo.toApi();
	}
	
	@Override
	@Transactional
	public Long newRepo(Repo repo) {
		RESTAssert.assertNotNull(repo);
		RESTAssert.assertNotNull(repo.getName());
		ERepo r = this.repoDAO.findByName(repo.getName());
		RESTAssert.assertTrue(r == null);
		ERepo g = this.repoHandler.createEntity(repo);
		return g.getId();
	}
	
	@Override
	@Transactional
	public void edit(Repo repo) {
		RESTAssert.assertNotNull(repo);
		RESTAssert.assertNotNull(repo.getId());
		ERepo g = this.repoDAO.findById(repo.getId());
		RESTAssert.assertNotNull(g);
		this.repoHandler.updateEntity(g, repo);
	}
	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotNull(name);
		ERepo g = this.repoDAO.findByName(name);
		RESTAssert.assertNotNull(g);
		this.repoHandler.deleteEntity(g);
	}
}
