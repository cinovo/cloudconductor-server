package de.cinovo.cloudconductor.server.rest.impl;

import de.cinovo.cloudconductor.api.interfaces.IDirectory;
import de.cinovo.cloudconductor.api.interfaces.IFile;
import de.cinovo.cloudconductor.api.model.ConfigFile;
import de.cinovo.cloudconductor.api.model.Directory;
import de.cinovo.cloudconductor.server.dao.*;
import de.cinovo.cloudconductor.server.model.*;
import de.cinovo.cloudconductor.server.rest.helper.AMConverter;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
import de.taimos.restutils.RESTAssert;
import de.taimos.springcxfdaemon.JaxRsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by janweisssieker on 18.11.16.
 */
@JaxRsComponent
public class DirectoryImpl extends ImplHelper implements IDirectory {
	
	@Autowired
	private IDirectoryDAO ddir;
	@Autowired
	private IPackageDAO dpkg;
	@Autowired
	private IServiceDAO dservice;
	@Autowired
	private AMConverter amc;
	@Autowired
	private ITemplateDAO dtemplate;


	
	
	@Override
	@Transactional
	public Directory[] get() {
		Set<Directory> result = new HashSet<>();
		for (EDirectory m : this.ddir.findList()) {
			result.add(MAConverter.fromModel(m));
		}
		return result.toArray(new Directory[result.size()]);
	}
	

	@Transactional
	public void save(String name, Directory directory) {
		this.assertName(name, directory);
		EDirectory cf = this.amc.toModel(directory);
		
		if ((directory.getPkg() != null) && !directory.getPkg().isEmpty()) {
			EPackage pkg = this.findByName(this.dpkg, directory.getPkg());
			cf.setPkg(pkg);
		} else {
			cf.setPkg(null);
		}
		
		if ((directory.getDependentServices() != null) && !directory.getDependentServices().isEmpty()) {
			List<EService> services = this.findByName(this.dservice, directory.getDependentServices());
			cf.setDependentServices(services);
		} else {
			cf.setDependentServices(null);
		}
		
		this.ddir.save(cf);
	}
	
	@Override
	@Transactional
	public Directory get(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory model = this.findByName(this.ddir, name);
		return MAConverter.fromModel(model);
	}

	

	
	@Override
	@Transactional
	public void delete(String name) {
		RESTAssert.assertNotEmpty(name);
		EDirectory model = this.ddir.findByName(name);
		this.assertModelFound(model);
		this.ddir.delete(model);
	}
	
	@Override
	@Transactional
	public Directory[] getDirectories(String template) {
		RESTAssert.assertNotEmpty(template);
		Set<Directory> result = new HashSet<>();
		ETemplate eTemplate = this.dtemplate.findByName(template);
		if ((eTemplate != null) && (eTemplate.getDirectory() != null)) {
			for (EDirectory m : eTemplate.getDirectory()) {
				result.add(MAConverter.fromModel(m));
			}
		}
		return result.toArray(new Directory[result.size()]);
	}

}
