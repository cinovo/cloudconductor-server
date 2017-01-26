package de.cinovo.cloudconductor.server.test.repo;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cinovo.cloudconductor.api.lib.helper.MapperFactory;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.repo.indexer.RPMIndexer;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 *
 */
public class IndexerTest {
	
	private static class ClasspathProvider implements IRepoProvider {
		
		@Override
		public List<RepoEntry> getEntries(String folder) {
			return null;
		}
		
		@Override
		public RepoEntry getEntry(String key) {
			RepoEntry fil = new RepoEntry();
			fil.setName(key);
			fil.setDirectory(false);
			fil.setModified(new Date());
			fil.setSize(0L);
			fil.setChecksum(UUID.randomUUID().toString());
			return fil;
		}
		
		@Override
		public InputStream getEntryStream(String key) {
			System.out.println("Loading..." + key);
			return this.getClass().getClassLoader().getResourceAsStream(key);
		}
		
		@Override
		public boolean isListable() {
			return false;
		}
		
		@Override
		public String getRepoName() {
			return "TESTREPO";
		}
		
	}
	
	
	/**
	 * @throws Exception on error
	 */
	@Test
	public void testRPM1() throws Exception {
		RPMIndexer indexer = new RPMIndexer();
		
		Set<PackageVersion> repoIndex = indexer.getRepoIndex(new ClasspathProvider());
		ObjectMapper om = MapperFactory.createDefault();
		for (PackageVersion pv : repoIndex) {
			System.out.println(om.writeValueAsString(pv));
		}
	}
	
}
