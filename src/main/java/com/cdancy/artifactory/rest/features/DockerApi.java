/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.artifactory.rest.features;

import com.cdancy.artifactory.rest.domain.docker.PromoteImage;
import com.cdancy.artifactory.rest.filters.ArtifactoryAuthenticationFilter;
import org.jclouds.Fallbacks;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/docker")
@RequestFilters(ArtifactoryAuthenticationFilter.class)
public interface DockerApi {

   @Named("docker:promote")
   @Consumes(MediaType.TEXT_PLAIN)
   @Path("/{repoKey}/v2/promote")
   @Fallback(Fallbacks.FalseOnNotFoundOr404.class)
   @POST
   boolean promote(@PathParam("repoKey") String repoKey, @BinderParam(BindToJsonPayload.class) PromoteImage promote);

   @Named("docker:list-repositories")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{repoKey}/_catalog")
   @SelectJson({"repositories"})
   @GET
   List<String> repositories(@PathParam("repoKey") String repoKey);
}
