package de.cinovo.cloudconductor.server.repo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.cinovo.cloudconductor.server.repo.provider.AWSS3Provider;
import de.cinovo.cloudconductor.server.repo.provider.FileProvider;
import de.cinovo.cloudconductor.server.repo.provider.HTTPProvider;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;

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
			EPackageServer server;
			if (group.getPrimaryServer() != null) {
				server = group.getPrimaryServer();
			} else {
				server = group.getPackageServers().iterator().next();
			}
			if (server != null) {
				switch (server.getProviderType()) {
				case AWSS3:
					return new AWSS3Provider(server);
				case FILE:
					return new FileProvider(server);
				case HTTP:
					return new HTTPProvider(server);
				default:
					break;
				}
			}
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
