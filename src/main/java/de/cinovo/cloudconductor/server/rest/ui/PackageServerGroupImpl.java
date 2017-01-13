package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackageServerGroup;
import de.cinovo.cloudconductor.api.model.PackageServerGroup;
import de.cinovo.cloudconductor.server.dao.IPackageServerGroupDAO;
import de.cinovo.cloudconductor.server.handler.PackageServerHandler;
import de.cinovo.cloudconductor.server.model.EPackageServerGroup;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class PackageServerGroupImpl implements IPackageServerGroup {

    @Autowired
    private IPackageServerGroupDAO packageServerGroupDAO;

    @Autowired
    private PackageServerHandler packageServerHandler;


    @Override
    @Transactional
    public PackageServerGroup[] get() {
        List<EPackageServerGroup> findList = this.packageServerGroupDAO.findList();
        Set<PackageServerGroup> result = new HashSet<>();
        for(EPackageServerGroup psg : findList) {
            result.add(psg.toApi());
        }
        return result.toArray(new PackageServerGroup[result.size()]);
    }

    @Override
    @Transactional
    public PackageServerGroup get(Long id) {
        RESTAssert.assertNotNull(id);
        EPackageServerGroup psg = this.packageServerGroupDAO.findById(id);
        RESTAssert.assertNotNull(psg);
        return psg.toApi();
    }

    @Override
    @Transactional
    public Long newGroup(PackageServerGroup group) {
        RESTAssert.assertNotNull(group);
        RESTAssert.assertNotNull(group.getName());
        EPackageServerGroup g = this.packageServerHandler.createEntity(group);
        return g.getId();
    }


    @Override
    @Transactional
    public void edit(PackageServerGroup group) {
        RESTAssert.assertNotNull(group);
        RESTAssert.assertNotNull(group.getId());
        EPackageServerGroup g = this.packageServerGroupDAO.findById(group.getId());
        RESTAssert.assertNotNull(g);
        this.packageServerHandler.updateEntity(g, group);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RESTAssert.assertNotNull(id);
        EPackageServerGroup g = this.packageServerGroupDAO.findById(id);
        RESTAssert.assertNotNull(g);
        this.packageServerHandler.deleteEntity(g);
    }
}
