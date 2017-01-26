package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.Template;

import javax.ws.rs.*;
import java.util.Set;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("template")
public interface ITemplate {

	/**
	 * @return set of service objects
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	Set<Template> get();

	/**
	 * @param template the template to save
	 */
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	void save(Template template);

	/**
	 * @param templateName the template name
	 */
	@DELETE
	@Path("/{template}")
	@Produces(MediaType.APPLICATION_JSON)
	void delete(@PathParam("template") String templateName);

	/**
	 * @param templateName the template name
	 * @return the template
	 */
	@GET
	@Path("/{template}")
	@Produces(MediaType.APPLICATION_JSON)
	Template get(@PathParam("template") String templateName);

	/**
	 * @param templateName the template name
	 * @param packageName the package to update
	 * @return the updated template
	 */
	@PUT
	@Path("/{template}/package/{package}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Template updatePackage(@PathParam("template") String templateName, @PathParam("package") String packageName);

	/**
	 * @param templateName the template name
	 * @param packageName the package to remove
	 * @return the updated template
	 */
	@DELETE
	@Path("/{template}/package/{package}")
	@Produces(MediaType.APPLICATION_JSON)
	Template deletePackage(@PathParam("template") String templateName, @PathParam("package") String packageName);

	/**
	 * @return set of service objects
	 */
	@GET
	@Path("/{template}/agentoption")
	@Produces(MediaType.APPLICATION_JSON)
	AgentOption getAgentOption(@PathParam("template") String templateName);

	/**
	 * @return set of service objects
	 */
	@PUT
	@Path("/{template}/agentoption")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	AgentOption saveAgentOption(@PathParam("template") String templateName, AgentOption option);
}
