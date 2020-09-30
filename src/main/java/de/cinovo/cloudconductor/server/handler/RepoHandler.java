package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Repo;
import de.cinovo.cloudconductor.api.model.RepoMirror;
import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.dao.IRepoMirrorDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.indexer.IRepoIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.IndexFileIndexer;
import de.cinovo.cloudconductor.server.repo.indexer.RPMIndexer;
import de.cinovo.cloudconductor.server.repo.provider.AWSS3Provider;
import de.cinovo.cloudconductor.server.repo.provider.FileProvider;
import de.cinovo.cloudconductor.server.repo.provider.HTTPProvider;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.cinovo.cloudconductor.server.tasks.IServerRepoTaskHandler;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import java.util.Set;
import java.util.stream.Collectors;

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
	 * @param repoName the name of the repo
	 * @return provider for given repo
	 */
	@Transactional
	public IRepoProvider findRepoProvider(String repoName) {
		ERepo erepo = this.repoDAO.findByName(repoName);
		if (erepo == null) {
			return null;
		}
		ERepoMirror primaryMirror = this.findPrimaryMirror(erepo);
		return this.findRepoProvider(primaryMirror);
	}

	/**
	 * @param mirror the mirror you want the repo provider for
	 * @return the repo provider
	 */
	public IRepoProvider findRepoProvider(ERepoMirror mirror) {
		if (mirror == null) {
			return null;
		}
		try {
			switch(mirror.getProviderType()) {
				case AWSS3:
					return new AWSS3Provider(mirror);
				case FILE:
					return new FileProvider(mirror);
				case HTTP:
					return new HTTPProvider(mirror);
				default:
					return null;
			}
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * @param mirror the mirror you want the repo indexer for
	 * @return the repo indexer
	 */
	public IRepoIndexer findRepoIndexer(ERepoMirror mirror) {
		if(mirror == null) {
			return null;
		}
		switch(mirror.getIndexerType()) {
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
	public ERepo updatePrimaryMirrorOfRepo(String repoName, Long id) {
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
	public ERepoMirror createEntity(RepoMirror mirror) throws WebApplicationException {
		String repoName = mirror.getRepo();
		ERepo erepo = this.repoDAO.findByName(repoName);
		if (erepo == null) {
			throw new NotFoundException(String.format("Repo '%s' not found!", repoName));
		}

		ERepoMirror emirror = new ERepoMirror();
		emirror = this.fillFields(emirror, erepo, mirror);
		RESTAssert.assertNotNull(emirror);

		return this.repoMirrorDAO.save(emirror);
	}

	/**
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
	 * @param mirror  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	@Transactional
	public ERepoMirror updateEntity(ERepoMirror emirror, RepoMirror mirror) throws WebApplicationException {
		String repoName = mirror.getRepo();
		ERepo erepo = this.repoDAO.findByName(repoName);
		if(erepo == null) {
			throw new NotFoundException(String.format("Repo '%s' not found!", repoName));
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
		return this.templateDAO.countUsingRepo(repo) > 0;
	}

	/**
	 * @param newRepo the new repository
	 * @return the saved repository
	 */
	@Transactional
	public ERepo createRepo(Repo newRepo) {
		RESTAssert.assertFalse(this.repoDAO.exists(newRepo.getName()));

		ERepo eRepo = new ERepo();
		eRepo = this.fillFields(eRepo, newRepo);
		ERepo savedRepo = this.repoDAO.save(eRepo);
		RESTAssert.assertNotNull(savedRepo);
		this.repoTaskHandler.newRepo(savedRepo);
		return savedRepo;
	}

	/**
	 * @param updatedRepo the updated Repo
	 */
	@Transactional
	public void updateRepo(Repo updatedRepo) {
		ERepo erepo = this.repoDAO.findById(updatedRepo.getId());
		RESTAssert.assertNotNull(erepo);

		ERepo eRepo = this.fillFields(erepo, updatedRepo);
		ERepo saved =  this.repoDAO.save(eRepo);
		RESTAssert.assertNotNull(saved);

		this.repoTaskHandler.newRepo(saved);
	}

	/**
	 * Deletes a repo and all its mirrors
	 *
	 * @param erepo the repo to delete
	 */
	public void deleteEntity(ERepo erepo) {
		if (this.checkIfInUse(erepo)) {
			throw new WebApplicationException(String.format("Repository '%s' is still used by a template!", erepo.getName()), Status.CONFLICT);
		}

		long id = erepo.getId();
		this.repoMirrorDAO.deleteForRepo(erepo);
		this.repoDAO.delete(erepo);
		this.repoTaskHandler.deleteRepo(id);
	}

	/**
	 * @param repo the repo
	 * @return the primary mirror of that repo
	 */
	public ERepoMirror findPrimaryMirror(ERepo repo) {
		return repo.getRepoMirrors().stream().filter(m -> m.getId().equals(repo.getPrimaryMirrorId())).findFirst().orElse(null);
	}

	private ERepo fillFields(ERepo eRepo, Repo repo) {
		if((repo.getName() != null) && !repo.getName().isEmpty()) {
			eRepo.setName(repo.getName());
		}
		Set<Long> mirrorIds = repo.getMirrors().stream().map(RepoMirror::getId).collect(Collectors.toSet());
		eRepo.setRepoMirrors(this.repoMirrorDAO.findByIds(mirrorIds));
		eRepo.setPrimaryMirrorId(repo.getPrimaryMirror());
		if((repo.getMirrors() != null) && (this.findPrimaryMirror(eRepo) == null)) {
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
