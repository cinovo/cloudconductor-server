package de.cinovo.cloudconductor.api.interfaces;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.ConfigValue;

import javax.ws.rs.*;
import java.util.Collection;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IConfigValue {

    /**
     * @return a collection of known templates
     */
    @GET
    Collection<String> getAvailableTemplates();

    /**
     * Returns configuration of the given template as Key-Value Pairs
     *
     * @param template the template name
     * @return set of stacked config values
     */
    @GET
    @Path("/{template}")
    Collection<ConfigValue> get(@PathParam("template") String template);

    /**
     * Returns all configuration key of a template in a non stacked variant
     *
     * @param template the template name
     * @return set of config values
     */
    @GET
    @Path("/{template}/unstacked")
    Collection<ConfigValue> getUnstacked(@PathParam("template") String template);

    /**
     * Returns configuration of the given service within the template as Key-Value Pairs
     *
     * @param template the template name
     * @param service  the name of the service
     * @return map of key value pairs representing the configuration of the service within the template
     */
    @GET
    @Path("/{template}/{service}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVAARGS, MediaType.APPLICATION_JAVAPROPS})
    Collection<ConfigValue> get(@PathParam("template") String template, @PathParam("service") String service);

    /**
     * Returns the value for a key of the given service within the template as Key-Value Pairs
     *
     * @param template the template name
     * @param service  the name of the service
     * @param key      the name of the key
     * @return the value of the key of the service within the template
     */
    @GET
    @Path("/{template}/{service}/{key}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVAARGS, MediaType.APPLICATION_JAVAPROPS})
    String get(@PathParam("template") String template, @PathParam("service") String service, @PathParam("key") String key);

    /**
     * Adds a new key-value pair to the configuration of a service within a template
     *
     * @param config the config value
     */
    @PUT
    void save(ConfigValue config);

    /**
     * Delete a ConfigValue
     *
     * @param template the template name
     * @param service  the name of the service
     * @param key      the name of the key
     */
    @DELETE
    @Path("/{template}/{service}/{key}")
    void delete(@PathParam("template") String template, @PathParam("service") String service, @PathParam("key") String key);

}
