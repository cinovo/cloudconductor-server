package de.cinovo.cloudconductor.server.repo.indexer;

import java.util.Set;

import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;

public interface IRepoIndexer {
	
	/**
	 * @param provider the {@link IRepoProvider} to get Index
	 * @return the new repo index or null no new one is available
	 */
	Set<PackageVersion> getRepoIndex(IRepoProvider provider);

}
