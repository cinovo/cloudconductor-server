package de.cinovo.cloudconductor.server.repo;

import de.cinovo.cloudconductor.server.dao.IRepoDAO;
import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.model.ERepo;
import de.cinovo.cloudconductor.server.model.ERepoMirror;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class RepoImpl implements IRepo {
	
	@Autowired
	private IRepoDAO repoDAO;
	@Autowired
	private RepoHandler repoHandler;
	
	
	@Override
	public Response get(String repo, String file) {
		if ((repo == null) || repo.isEmpty()) {
			throw new NotFoundException();
		}
		IRepoProvider provider = this.findProvider(repo);
		if (provider == null) {
			throw new NotFoundException();
		}
		
		if (file.isEmpty() || file.endsWith("/")) {
			if (!provider.isListable()) {
				// return this.resultNoList(file);
			}
			List<RepoEntry> list = provider.getEntries(file);
			if (list != null) {
				// return this.resultList(file, list);
			}
			throw new NotFoundException();
		}
		
		RepoEntry entry = provider.getEntry(file);
		if (entry != null) {
			return this.resultStream(provider.getEntryStream(file), entry);
		}
		throw new NotFoundException();
	}
	
	private IRepoProvider findProvider(String repo) {
		ERepo erepo = this.repoDAO.findByName(repo);
		if (erepo != null) {
			ERepoMirror primaryMirror = this.repoHandler.findPrimaryMirror(erepo);
			return this.repoHandler.findRepoProvider(primaryMirror);
		}
		return null;
	}
	
	private Response resultStream(final InputStream stream, RepoEntry entry) {
		StreamingOutput out = new StreamingOutput() {
			
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				StreamUtils.copy(stream, output);
				output.flush();
				output.close();
			}
		};
		return Response.ok(out, entry.getContentType()).header("Content-Length", entry.getSize()).build();
	}
	
}
