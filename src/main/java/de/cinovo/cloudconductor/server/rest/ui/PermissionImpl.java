package de.cinovo.cloudconductor.server.rest.ui;

import de.cinovo.cloudconductor.api.enums.UserPermissions;
import de.cinovo.cloudconductor.api.interfaces.IPermission;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@JaxRsComponent
public class PermissionImpl implements IPermission {

	@Override
	@Transactional
	public Set<UserPermissions> getPermissions() {
		return new HashSet<UserPermissions>(Arrays.asList(UserPermissions.values()));
	}

}
