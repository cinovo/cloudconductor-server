package de.cinovo.cloudconductor.server.handler;

import de.cinovo.cloudconductor.api.model.Template;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ETemplate;
import de.cinovo.cloudconductor.server.util.comparators.PackageVersionComparator;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class TemplateHandler {

	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IPackageServerDAO packageServerDAO;

	/**
	 * @param t the data
	 * @return the saved entity
	 * @throws WebApplicationException on error
	 */
	public ETemplate createEntity(Template t) throws WebApplicationException {
		ETemplate et = new ETemplate();
		et = this.fillFields(et, t);
		RESTAssert.assertNotNull(et);
		return this.templateDAO.save(et);
	}

	/**
	 * @param et the entity to update
	 * @param t  the update data
	 * @return the updated, saved entity
	 * @throws WebApplicationException on error
	 */
	public ETemplate updateEntity(ETemplate et, Template t) throws WebApplicationException {
		et = this.fillFields(et, t);
		RESTAssert.assertNotNull(et);
		return this.templateDAO.save(et);
	}

	public void updateAllPackages() {
		for(ETemplate t : this.templateDAO.findList()) {
			this.updateAllPackages(t);
		}
	}

	public void updateAllPackages(ETemplate template) {
		PackageVersionComparator versionComp = new PackageVersionComparator();

		if((template.getAutoUpdate() == null) || !template.getAutoUpdate()) {
			return;
		}

		List<EPackageVersion> list = new ArrayList<>(template.getPackageVersions());

		for(EPackageVersion version : template.getPackageVersions()) {
			List<EPackageVersion> eversion = new ArrayList<>(version.getPkg().getVersions());
			Collections.sort(eversion, versionComp);
			EPackageVersion newest = eversion.get(eversion.size() - 1);
			if(!newest.equals(version)) {
				list.remove(version);
				list.add(newest);
			}
		}

		template.setPackageVersions(list);
		this.templateDAO.save(template);
	}

	private ETemplate fillFields(ETemplate et, Template t) {
		et.setName(t.getName());
		et.setDescription(t.getDescription());
		et.setPackageServers(new ArrayList<EPackageServer>());
		for(EPackageServer serv : this.packageServerDAO.findList()) {
			if(t.getPackageServers().contains(serv.getPath()) || t.getPackageServers().contains(serv.getPath().substring(6))) {
				et.getPackageServers().add(serv);
				break;
			}
		}
		return et;
	}
}
