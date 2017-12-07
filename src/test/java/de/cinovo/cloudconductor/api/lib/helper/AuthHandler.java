package de.cinovo.cloudconductor.api.lib.helper;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.model.Authentication;
import org.apache.http.HttpResponse;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class AuthHandler extends AbstractApiHandler {

	public AuthHandler(String cloudconductorUrl) {
		super(cloudconductorUrl, null);
	}

	public String auth() {
		Authentication auth = new Authentication();
		auth.setUsername("admin");
		auth.setPassword("admin");
		try {
			HttpResponse response = this._put("/auth", auth);
			AbstractApiHandler.assertSuccess("/auth", response);
			return this.objectFromResponse(response, String.class);
		} catch(CloudConductorException e) {
			return null;
		}

	}
}
