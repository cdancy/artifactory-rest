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

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.cdancy.artifactory.rest.binders.BindMatrixPropertiesToPath;
import com.cdancy.artifactory.rest.domain.artifact.Artifact;
import com.cdancy.artifactory.rest.domain.error.RequestStatus;
import static com.cdancy.artifactory.rest.fallbacks.ArtifactoryFallbacks.RequestStatusFromError;
import com.cdancy.artifactory.rest.filters.ArtifactoryAuthentication;

import com.cdancy.artifactory.rest.functions.HttpResponseToFile;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.*;

import com.google.common.net.HttpHeaders;

import java.io.File;
import java.util.List;
import java.util.Map;

@Path("/")
@RequestFilters(ArtifactoryAuthentication.class)
public interface ArtifactApi {

   @Named("artifact:deploy")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/{repoKey}/{itemPath}")
   @Headers(keys = HttpHeaders.CONTENT_TYPE, values = MediaType.APPLICATION_OCTET_STREAM)
   @PUT
   Artifact deployArtifact(@PathParam("repoKey") String repoKey, @PathParam("itemPath") String itemPath,
                           Payload inputStream, @Nullable @BinderParam(BindMatrixPropertiesToPath.class) Map<String, List<String>> properties);

   @Named("artifact:retrieve")
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/{repoKey}/{itemPath}")
   @ResponseParser(HttpResponseToFile.class)
   @Fallback(NullOnNotFoundOr404.class)
   @GET
   File retrieveArtifact(@PathParam("repoKey") String repoKey, @PathParam("itemPath") String itemPath,
                         @Nullable @BinderParam(BindMatrixPropertiesToPath.class) Map<String, List<String>> properties);

   @Named("artifact:delete")
   @Path("/{repoKey}/{itemPath}")
   @Fallback(FalseOnNotFoundOr404.class)
   @DELETE
   boolean deleteArtifact(@PathParam("repoKey") String repoKey, @PathParam("itemPath") String itemPath);

   @Named("artifact:copy")
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/api/copy/{repoKey}/{itemPath}")
   @QueryParams(keys = { "failFast", "suppressLayouts" }, values = { "1", "0" })
   @Fallback(RequestStatusFromError.class)
   @POST
   RequestStatus copyArtifact(@PathParam("repoKey") String sourceRepo, @PathParam("itemPath") String sourcePath, @QueryParam("to") String targetPath);
}
