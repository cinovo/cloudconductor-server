package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.interfaces.IPackageServer;
import de.cinovo.cloudconductor.api.model.PackageServer;
import de.cinovo.cloudconductor.server.dao.IPackageServerDAO;
import de.cinovo.cloudconductor.server.handler.PackageServerHandler;
import de.cinovo.cloudconductor.server.model.EPackageServer;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@JaxRsComponent
public class PackageServerImpl implements IPackageServer {

    @Autowired
    private IPackageServerDAO packageServerDAO;
    @Autowired
    private PackageServerHandler packageServerHandler;

    @Override
    @Transactional
    public PackageServer get(Long id) {
        EPackageServer eps = this.packageServerDAO.findById(id);
        if(eps == null) {
            throw new NotFoundException();
        }
        return eps.toApi();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        this.packageServerDAO.deleteById(id);
    }

    @Override
    @Transactional
    public Long newServer(PackageServer ps) {
        RESTAssert.assertNotNull(ps);
        RESTAssert.assertNotNull(ps.getServerGroup());
        EPackageServer eps = this.packageServerHandler.createEntity(ps);
        RESTAssert.assertNotNull(eps);
        eps = this.packageServerDAO.save(eps);
        return eps.getId();
    }

    @Override
    @Transactional
    public void editServer(PackageServer ps) {
        RESTAssert.assertNotNull(ps);
        RESTAssert.assertNotNull(ps.getServerGroup());
        EPackageServer eps = this.packageServerDAO.findById(ps.getId());
        RESTAssert.assertNotNull(eps);
        eps = this.packageServerHandler.updateEntity(eps, ps);
        RESTAssert.assertNotNull(eps);
        this.packageServerDAO.save(eps);
    }

}
