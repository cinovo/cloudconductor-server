package de.cinovo.cloudconductor.server.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.api.model.RepoMirror;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.IndexFileIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.RPMIndexer;
import de.cinovo.cloudconductor.server.repo.provider.AWSS3Provider;
import de.cinovo.cloudconductor.server.repo.provider.FileProvider;
import de.cinovo.cloudconductor.server.repo.provider.HTTPProvider;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.cinovo.cloudconductor.server.tasks.IServerRepoTaskHandler;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class RepoHandler {
	
	@Autowired
	private IRepoMirrorDAO repoMirrorDAO;
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private ITemplateDAO templateDAO;
	
	@Autowired
	private IServerRepoTaskHandler repoTaskHandler;
	
	
	/**
	 * @param mirror the mirror you want the repo provider for
	 * @return the repo provider
	 */
	public IRepoProvider findRepoProvider(ERepoMirror mirror) {
		if (mirror == null) {
			return null;
		}
		switch (mirror.getProviderType()) {
		case AWSS3:
			return new AWSS3Provider(mirror);
		case FILE:
			return new FileProvider(mirror);
		case HTTP:
			return new HTTPProvider(mirror);
		default:
			return null;
		}
	}
	
	/**
	 * @param mirror the mirror you want the repo indexer for
	 * @return the repo indexer
	 */
	public IRepoIndexer findRepoIndexer(ERepoMirror mirror) {
		if (mirror == null) {
			return null;
		}
		switch (mirror.getIndexerType()) {
		case FILE:
			return new IndexFileIndexer();
		case RPM:
			return new RPMIndexer();
		default:
			return null;
		}
	}
	
	/**
	 * @param newMirror the repo mirror to save
	 * @return the saved repo mirror
	 */
	public ERepoMirror createMirror(RepoMirror newMirror) {
		ERepoMirror emirror = this.createEntity(newMirror);
		
		ERepo erepo = this.updatePrimaryMirrorOfRepo(newMirror.getRepo(), emirror.getId());
		// trigger repo index tasks
		this.repoTaskHandler.newRepo(erepo);
		
		return emirror;
	}
	
	@Transactional
	private ERepo updatePrimaryMirrorOfRepo(String repoName, Long id) {
		RESTAssert.assertNotEmpty(repoName);
		RESTAssert.assertNotNull(id);
		
		ERepo erepo = this.repoDAO.findByName(repoName);
		
		// set to primary if this is the first mirror
		if (erepo.getRepoMirrors().size() == 1) {
			erepo.setPrimaryMirrorId(id);
			erepo = this.repoDAO.save(erepo);
		}
		
		return erepo;
	}
	
	/**
	 * @param mirror the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	@Transactional
	private ERepoMirror createEntity(RepoMirror mirror) throws WebApplicationException {
		String repoName = mirror.getRepo();
		ERepo erepo = this.repoDAO.findByName(repoName);
		if (erepo == null) {
			throw new NotFoundException("Repo '" + repoName + "' not found!");
		}
		
		ERepoMirror emirror = new ERepoMirror();
		emirror = this.fillFields(emirror, erepo, mirror);
		RESTAssert.assertNotNull(emirror);
		
		return this.repoMirrorDAO.save(emirror);
	}
	
	/**
	 * 
	 * @param updatedMirror the updated mirror
	 * @return the saved mirror
	 */
	public ERepoMirror updateMirror(RepoMirror updatedMirror) {
		ERepoMirror emirror = this.repoMirrorDAO.findById(updatedMirror.getId());
		RESTAssert.assertNotNull(emirror);
		
		ERepoMirror saved = this.updateEntity(emirror, updatedMirror);
		RESTAssert.assertNotNull(saved);
		
		ERepo erepo = this.repoDAO.findByName(updatedMirror.getRepo());
		this.repoTaskHandler.newRepo(erepo);
		
		return saved;
	}
	
	/**
	 * @param emirror the entity to update
	 * @param mirror the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	@Transactional
	private ERepoMirror updateEntity(ERepoMirror emirror, RepoMirror mirror) throws WebApplicationException {
		String repoName = mirror.getRepo();
		ERepo erepo = this.repoDAO.findByName(repoName);
		if (erepo == null) {
			throw new NotFoundException("Repo '" + repoName + "' not found!");
		}
		
		ERepoMirror entity = this.fillFields(emirror, erepo, mirror);
		RESTAssert.assertNotNull(entity);
		return this.repoMirrorDAO.save(entity);
	}
	
	/**
	 * @param repo the repo entity
	 * @return whether the mirror is in use by a template or not
	 */
	public boolean checkIfInUse(ERepo repo) {
		List<ETemplate> templates = this.templateDAO.findByRepo(repo);
		return !templates.isEmpty();
	}
	
	/**
	 * 
	 * @param newRepo the new repository
	 * @return the saved repository
	 */
	public ERepo createRepo(Repo newRepo) {
		ERepo erepo = this.createEntity(newRepo);
		this.repoTaskHandler.newRepo(erepo);
		
		return erepo;
	}
	
	/**
	 * @param repo the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	@Transactional
	private ERepo createEntity(Repo repo) throws WebApplicationException {
		ERepo existing = this.repoDAO.findByName(repo.getName());
		RESTAssert.assertTrue(existing == null);
		
		ERepo newRepo = new ERepo();
		newRepo = this.fillFields(newRepo, repo);
		RESTAssert.assertNotNull(newRepo);
		return this.repoDAO.save(newRepo);
	}
	
	/**
	 * 
	 * @param updatedRepo the updated Repo
	 * @return the saved repo
	 */
	public ERepo updateRepo(Repo updatedRepo) {
		ERepo saved = this.updateEntity(updatedRepo);
		this.repoTaskHandler.newRepo(saved);
		
		return saved;
	}
	
	/**
	 * @param erepo the entity to update
	 * @param repo the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	@Transactional
	private ERepo updateEntity(Repo repo) throws WebApplicationException {
		ERepo erepo = this.repoDAO.findById(repo.getId());
		RESTAssert.assertNotNull(erepo);
		
		ERepo entity = this.fillFields(erepo, repo);
		RESTAssert.assertNotNull(entity);
		ERepo saved = this.repoDAO.save(entity);
		
		return saved;
	}
	
	/**
	 * Deletes a repo and its sub mirrors
	 *
	 * @param erepo the repo to delete
	 * @throws WebApplicationException on error
	 */
	public void deleteEntity(ERepo erepo) throws WebApplicationException {
		if (this.checkIfInUse(erepo)) {
			throw new WebApplicationException("Repository '" + erepo.getName() + "' is still used by a template!", Status.CONFLICT);
		}
		if ((erepo.getRepoMirrors() != null) && !erepo.getRepoMirrors().isEmpty()) {
			for (ERepoMirror mirror : erepo.getRepoMirrors()) {
				this.repoMirrorDAO.delete(mirror);
			}
		}
		this.repoDAO.delete(erepo);
		this.repoTaskHandler.deleteRepo(erepo.getId());
	}
	
	/**
	 * @param repo the repo
	 * @return list of all mirrors provided by the repo
	 */
	public List<ERepoMirror> getRepoMirrors(Repo repo) {
		List<ERepoMirror> mirrors = new ArrayList<>();
		for (RepoMirror mirror : repo.getMirrors()) {
			ERepoMirror emirror = this.repoMirrorDAO.findById(mirror.getId());
			if (emirror != null) {
				mirrors.add(emirror);
			}
		}
		return mirrors;
	}
	
	/**
	 * @param repo the repo
	 * @return the primary mirror of that repo
	 */
	public ERepoMirror findPrimaryMirror(ERepo repo) {
		for (ERepoMirror mirror : repo.getRepoMirrors()) {
			if (Objects.equals(mirror.getId(), repo.getPrimaryMirrorId())) {
				return mirror;
			}
		}
		return repo.getRepoMirrors().isEmpty() ? null : repo.getRepoMirrors().iterator().next();
	}
	
	private ERepo fillFields(ERepo eRepo, Repo repo) {
		if ((repo.getName() != null) && !repo.getName().isEmpty()) {
			eRepo.setName(repo.getName());
		}
		List<ERepoMirror> mirrors = this.getRepoMirrors(repo);
		eRepo.setRepoMirrors(mirrors);
		eRepo.setPrimaryMirrorId(repo.getPrimaryMirror());
		if ((repo.getMirrors() != null) && (this.findPrimaryMirror(eRepo) == null)) {
			eRepo.setPrimaryMirrorId(null);
		}
		return eRepo;
	}
	
	private ERepoMirror fillFields(ERepoMirror emirror, ERepo erepo, RepoMirror mirror) {
		emirror.setRepo(erepo);
		emirror.setAccessKeyId(mirror.getAccessKeyId());
		emirror.setAwsRegion(mirror.getAwsRegion());
		emirror.setBasePath(mirror.getBasePath());
		emirror.setBucketName(mirror.getBucketName());
		emirror.setDescription(mirror.getDescription());
		emirror.setIndexerType(mirror.getIndexerType());
		emirror.setPath(mirror.getPath());
		emirror.setProviderType(mirror.getProviderType());
		emirror.setSecretKey(mirror.getSecretKey());
		return emirror;
	}
}
