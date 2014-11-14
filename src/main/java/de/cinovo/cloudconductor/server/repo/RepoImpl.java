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

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class RepoImpl extends AWebPage implements IRepo {
	
	@Autowired
	private IRepoProvider provider;
	
	
	@Override
	public Response get(String file) {
		if (file.isEmpty() || file.endsWith("/")) {
			if (!this.provider.isListable()) {
				return this.resultNoList(file);
			}
			List<RepoEntry> list = this.provider.getEntries(file);
			if (list != null) {
				return this.resultList(file, list);
			}
			throw new NotFoundException();
		}
		RepoEntry entry = this.provider.getEntry(file);
		if (entry != null) {
			return this.resultStream(this.provider.getEntryStream(file), entry);
		}
		throw new NotFoundException();
	}
	
	private Response resultNoList(String folder) {
		CSViewModel view = this.createView("notListable");
		view.addModel("folder", folder);
		return Response.ok(view.render(), MediaType.TEXT_HTML).build();
	}

	private Response resultList(String folder, List<RepoEntry> entries) {
		CSViewModel view = this.createView("list");
		view.addModel("folder", folder);
		view.addModel("files", entries);
		view.addModel("byteTool", new ByteTool());
		return Response.ok(view.render(), MediaType.TEXT_HTML).build();
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

	@Override
	protected String getTemplateFolder() {
		return "repo";
	}

	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.links, "Package Repository", "/../repo/");
	}

	@Override
	protected String getNavElementName() {
		return "Repository";
	}
	
}
