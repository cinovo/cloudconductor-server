package de.cinovo.cloudconductor.server.repo;

import de.cinovo.cloudconductor.server.handler.RepoHandler;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
@JaxRsComponent
public class ReposProviderImpl implements IReposProvider {

	@Autowired
	private RepoHandler repoHandler;

	@Override
	public Response get(String repo, String file) {
		RESTAssert.assertNotEmpty(repo, Response.Status.NOT_FOUND);
		RESTAssert.assertNotEmpty(file, Response.Status.NOT_FOUND);
		RESTAssert.assertTrue(file.endsWith("/"));

		IRepoProvider provider = this.repoHandler.findRepoProvider(repo);
		RESTAssert.assertNotNull(provider, Response.Status.NOT_FOUND);
		
		RepoEntry entry = provider.getEntry(file);
		RESTAssert.assertNotNull(entry, Response.Status.NOT_FOUND);

		return this.resultStream(provider.getEntryStream(file), entry);
	}
	
	private Response resultStream(final InputStream stream, RepoEntry entry) {
		StreamingOutput out = output -> {
			StreamUtils.copy(stream, output);
			output.flush();
			output.close();
		};
		return Response.ok(out, entry.getContentType()).header("Content-Length", entry.getSize()).build();
	}
	
}
