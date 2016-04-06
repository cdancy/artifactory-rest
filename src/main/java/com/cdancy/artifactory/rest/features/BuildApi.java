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

import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import static com.cdancy.artifactory.rest.fallbacks.ArtifactoryFallbacks.RequestStatusFromError;
import com.cdancy.artifactory.rest.filters.ArtifactoryAuthentication;
import com.cdancy.artifactory.rest.options.PromoteBuildOptions;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(ArtifactoryAuthentication.class)
public interface BuildApi {

   @Named("build:promote")
   @Path("/build/promote/{buildName}/{buildNumber}")
   @Fallback(RequestStatusFromError.class)
   @POST
   RequestStatus promote(@PathParam("buildName") String buildName, @PathParam("buildNumber") int buildNumber,
                         @BinderParam(BindToJsonPayload.class) PromoteBuildOptions promoteBuildOptions);
}
