package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Settings;

import javax.ws.rs.*;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ISettings {

	/**
	 * @return set of service objects
	 */
	@GET
	Settings get();

	/**
	 * @param settings the settings to save
	 */
	@PUT
	void save(Settings settings);
}
