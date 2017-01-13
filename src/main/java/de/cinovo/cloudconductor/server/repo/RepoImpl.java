package de.cinovo.cloudconductor.server.repo;

import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.handler.PackageServerHandler;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
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
// TODO add angularjs view
public class RepoImpl implements IRepo {
	
	@Autowired
	private IPackageServerGroupDAO psgDAO;

	@Autowired
	private IPackageServerDAO psDAO;
	
	
	
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
		EPackageServerGroup group = this.psgDAO.findByName(repo);
		if (group != null) {
			EPackageServer server = null;
			if (group.getPrimaryServerId() != null) {
				server = this.psDAO.findById(group.getPrimaryServerId());
			}
			if(server == null) {
				server = group.getPackageServers().iterator().next();
			}
			return PackageServerHandler.findRepoProvider(server);
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
