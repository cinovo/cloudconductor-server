package de.cinovo.cloudconductor.server.repo.provider;

import java.io.InputStream;
import java.util.List;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

public interface IRepoProvider {
	
	public static final String INDEX_FILE = "index.c2";
	
	
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
	
	/**
	 * @return the content5 of the latest index or null if no newer index is available
	 */
	String getLatestIndex();

}
