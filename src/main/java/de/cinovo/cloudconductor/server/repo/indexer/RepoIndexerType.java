package de.cinovo.cloudconductor.server.repo.indexer;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public enum RepoIndexerType {
	/** no indexer chosen */
	NONE,
	/** {@link IndexFileIndexer} */
	FILE,
	/** {@link RPMIndexer} */
	RPM
	
}
