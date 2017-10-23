package de.cinovo.cloudconductor.server.rest.ui;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.api.interfaces.IRepoMirror;
import de.cinovo.cloudconductor.api.model.RepoMirror;
import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class RepoMirrorImpl implements IRepoMirror {
	
	@Autowired
	private IRepoMirrorDAO repoMirrorDAO;
	@Autowired
	private RepoHandler repoHandler;
	
	
	@Override
	@Transactional
	public RepoMirror get(Long id) {
		ERepoMirror emirror = this.repoMirrorDAO.findById(id);
		if (emirror == null) {
			throw new NotFoundException();
		}
		return emirror.toApi();
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		this.repoMirrorDAO.deleteById(id);
	}
	
	@Override
	@Transactional
	public Long newMirror(RepoMirror mirror) {
		RESTAssert.assertNotNull(mirror);
		RESTAssert.assertNotEmpty(mirror.getRepo());
		
		ERepoMirror emirror = this.repoHandler.createEntity(mirror);
		RESTAssert.assertNotNull(emirror);
		emirror = this.repoMirrorDAO.save(emirror);
		return emirror.getId();
	}
	
	@Override
	@Transactional
	public void editMirror(RepoMirror mirror) {
		RESTAssert.assertNotNull(mirror);
		RESTAssert.assertNotNull(mirror.getRepo());
		ERepoMirror emirror = this.repoMirrorDAO.findById(mirror.getId());
		RESTAssert.assertNotNull(emirror);
		emirror = this.repoHandler.updateEntity(emirror, mirror);
		RESTAssert.assertNotNull(emirror);
		this.repoMirrorDAO.save(emirror);
	}
	
}
