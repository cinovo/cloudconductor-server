package de.cinovo.cloudconductor.server.repo.provider;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

import java.io.InputStream;
import java.util.List;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
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
	
	/**
	 * @return <code>true</code> if listings are possible; <code>false</code> otherwise
	 */
	boolean isListable();
	
	/**
	 * @return the name of the repo
	 */
	String getRepoName();
	
}
