package de.cinovo.cloudconductor.server.repo.provider;

import java.io.InputStream;
import java.util.List;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

public interface IRepoProvider {
	
	List<RepoEntry> getEntries(String folder);
	
	RepoEntry getEntry(String key);
	
	InputStream getEntryStream(String key);

}
