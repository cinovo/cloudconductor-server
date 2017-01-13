package de.cinovo.cloudconductor.server.rest.shared;

import de.cinovo.cloudconductor.api.interfaces.IDirectory;
import de.cinovo.cloudconductor.api.model.Directory;
import de.cinovo.cloudconductor.server.dao.IDirectoryDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.handler.FileHandler;
import de.cinovo.cloudconductor.server.model.EDirectory;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by janweisssieker on 18.11.16.
 */
@JaxRsComponent
public class DirectoryImpl implements IDirectory {

	@Autowired
	private IDirectoryDAO directoryDAO;
	@Autowired
	private ITemplateDAO templateDAO;

	@Autowired
	private FileHandler fileHandler;


	@Override
	@Transactional
	public Directory[] get() {
		Set<Directory> result = new HashSet<>();
		for(EDirectory m : this.directoryDAO.findList()) {
			result.add(m.toApi());
		}
		return result.toArray(new Directory[result.size()]);
	}


	@Override
	@Transactional
	public void save(Directory directory) {
		RESTAssert.assertNotNull(directory);
		RESTAssert.assertNotNull(directory.getName());

		EDirectory eDirectory = this.directoryDAO.findByName(directory.getName());
		if(eDirectory == null) {
			this.fileHandler.createEntity(directory);
		} else {
			this.fileHandler.updateEntity(eDirectory, directory);
		}
	}

	@Override
	@Transactional
	public Directory get(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory model = this.directoryDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		return model.toApi();
	}


	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory model = this.directoryDAO.findByName(name);
		RESTAssert.assertNotNull(model);
		this.directoryDAO.delete(model);
	}

	@Override
	@Transactional
	public Directory[] getDirectories(String template) {
		RESTAssert.assertNotEmpty(template);
		Set<Directory> result = new HashSet<>();
		ETemplate eTemplate = this.templateDAO.findByName(template);
		if((eTemplate != null) && (eTemplate.getDirectory() != null)) {
			for(EDirectory m : eTemplate.getDirectory()) {
				result.add(m.toApi());
			}
		}
		return result.toArray(new Directory[result.size()]);
	}

}
