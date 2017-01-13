package de.cinovo.cloudconductor.api.interfaces;

/*
 * #%L
 * cloudconductor-api
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.api.model.Service;

import javax.ws.rs.*;
import java.util.Map;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Path("service")
public interface IService {

    /**
     * @return set of service objects
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Service[] get();

    /**
     * @param service the service name
     * @return the api object
     */
    @GET
    @Path("/{service}")
    @Produces(MediaType.APPLICATION_JSON)
    Service get(@PathParam("service") String service);

    /**
     * @param apiObject the service object
     */
    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    void save(Service apiObject);

    /**
     * @param service the service name
     */
    @DELETE
    @Path("/{service}")
    void delete(@PathParam("service") String service);

    /**
     * @param service the service name
     * @return map of template-package pairs
     */
    @GET
    @Path("/{service}/usage")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, String> getUsage(@PathParam("service") String service);
}
