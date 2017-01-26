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
public interface ISettings {

	/**
	 * @return set of service objects
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Settings get();

	/**
	 * @param settings the settings to save
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void save(Settings settings);
}
