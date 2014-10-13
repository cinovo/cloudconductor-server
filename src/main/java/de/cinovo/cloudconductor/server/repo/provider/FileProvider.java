package de.cinovo.cloudconductor.server.repo.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.server.repo.RepoEntry;

public class FileProvider implements IRepoProvider {

	@Value("${repo.basedir:static/yum/}")
	private String basedir;
	
	
	@Override
	public boolean isListable() {
		return true;
	}

	@Override
	public List<RepoEntry> getEntries(String folder) {
		File dir = new File(this.basedir + folder);
		if (!dir.isDirectory()) {
			return null;
		}
		List<RepoEntry> list = new ArrayList<RepoEntry>();
		for (File file : dir.listFiles()) {
			list.add(this.createEntry(file));
		}
		return list;
	}

	@Override
	public RepoEntry getEntry(String key) {
		File file = new File(this.basedir + key);
		if (file.exists()) {
			return this.createEntry(file);
		}
		return null;
	}
	
	private RepoEntry createEntry(File file) {
		RepoEntry e = new RepoEntry();
		e.setDirectory(file.isDirectory());
		e.setName(file.getName());
		e.setSize(file.length());
		e.setModified(new Date(file.lastModified()));
		return e;
	}

	@Override
	public InputStream getEntryStream(String key) {
		File file = new File(this.basedir + key);
		if (file.exists()) {
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// may not happen
			}
		}
		return null;
	}

}
