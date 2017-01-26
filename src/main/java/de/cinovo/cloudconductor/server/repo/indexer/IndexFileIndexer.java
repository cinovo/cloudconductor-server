package de.cinovo.cloudconductor.server.repo.indexer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 *
 */
public class IndexFileIndexer implements IRepoIndexer {

	/**
	 * Cloudconductor Index file
	 */
	public static final String INDEX_FILE = "index.c2";
	
	private RepoEntry latest;
	
	@Autowired
	private ObjectMapper mapper = MapperFactory.createDefault();
	
	
	@Override
	public Set<PackageVersion> getRepoIndex(IRepoProvider provider) {
		RepoEntry indexEntry = provider.getEntry(IndexFileIndexer.INDEX_FILE);
		if (indexEntry != null) {
			if (!indexEntry.hasChanged(this.latest)) {
				return null;
			}
			this.latest = indexEntry;
			try {
				String indexString = StreamUtils.copyToString(provider.getEntryStream(IndexFileIndexer.INDEX_FILE), Charset.forName("UTF-8"));
				CollectionType indexType = CollectionType.construct(Set.class, SimpleType.construct(PackageVersion.class));
				return this.mapper.readValue(indexString, indexType);
			} catch (IOException e) {
				throw new RuntimeException("Failed to read index", e);
			}
		}
		return null;
	}

}
