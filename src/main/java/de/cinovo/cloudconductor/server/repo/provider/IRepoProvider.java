package de.cinovo.cloudconductor.server.repo.provider;

import java.io.InputStream;
import java.util.List;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

public interface IRepoProvider {
	
	/**
	 * @param folder the name of the folder to search
	 * @return list of file meta data
	 */
	List<RepoEntry> getEntries(String folder);
	
	/**
	 * @param key the name of the file
	 * @return the file meta data
	 */
	RepoEntry getEntry(String key);
	
	/**
	 * @param key the name of the file
	 * @return the stream of the content of the given file
	 */
	InputStream getEntryStream(String key);

}
