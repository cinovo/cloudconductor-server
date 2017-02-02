package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.enums.ServiceState;
import de.cinovo.cloudconductor.api.model.Host;

import javax.ws.rs.*;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("/host")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IHost {

	/**
	 * @return list of hosts
	 */
	@GET
	List<Host> getHosts();

	/**
	 * @param hostName the host name
	 * @return the Host
	 */
	@GET
	@Path("/{host}")
	Host getHost(@PathParam("host") String hostName);

	/**
	 * @param hostName the host name
	 */
	@DELETE
	@Path("/{host}")
	void deleteHost(@PathParam("host") String hostName);

	/**
	 * @param hostName the host name
	 * @param serviceName the service name
	 * @param newState the new state
	 */
	@PUT
	@Path("/{host}/{service}")
	void setServiceState(@PathParam("host") String hostName, @PathParam("service") String serviceName, ServiceState newState);
}
