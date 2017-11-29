package de.cinovo.cloudconductor.server.rest.ui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.cinovo.cloudconductor.api.enums.UserPermissions;
import de.cinovo.cloudconductor.api.interfaces.IPermission;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@JaxRsComponent
public class PermissionImpl implements IPermission {

	@Override
	public Set<UserPermissions> getPermissions() {
		return new HashSet<UserPermissions>(Arrays.asList(UserPermissions.values()));
	}

}
