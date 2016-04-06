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

import com.cdancy.artifactory.rest.binders.BindListReposToPath;
import com.cdancy.artifactory.rest.binders.BindMapToPath;
import com.cdancy.artifactory.rest.domain.search.SearchBuildArtifacts;
import com.cdancy.artifactory.rest.domain.search.SearchResult;
import com.cdancy.artifactory.rest.parsers.ArtifactDownloadURIToList;
import com.cdancy.artifactory.rest.parsers.ArtifactURIToList;
import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.*;

import com.cdancy.artifactory.rest.domain.search.AQLResult;
import com.cdancy.artifactory.rest.filters.ArtifactoryAuthentication;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.List;
import java.util.Map;

@Path("/api/search")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(ArtifactoryAuthentication.class)
public interface SearchApi {

   @Named("search:aql")
   @Path("/aql")
   @Produces(MediaType.TEXT_PLAIN)
   @Payload("{aql_query}")
   @POST
   AQLResult aql(@PayloadParam("aql_query") String query);

   @Named("search:error-artifacts")
   @Path("/buildArtifacts")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("results")
   @POST
   List<SearchResult> buildArtifacts(@BinderParam(BindToJsonPayload.class) SearchBuildArtifacts searchBuildArtifacts);

   @Named("search:property-search")
   @Path("/prop")
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("results")
   @GET
   List<SearchResult> propertySearch(@BinderParam(BindMapToPath.class) Map<String, List<String>> properties, @Nullable @BinderParam(BindListReposToPath.class) List<String> repos);

   @Named("search:not-downloaded-since")
   @Path("/usage")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   @Produces(MediaType.APPLICATION_JSON)
   @SelectJson("results")
   @GET
   List<SearchResult> notDownloadedSince(@QueryParam("notUsedSince") long notUsedSince_ISO8601, @Nullable @QueryParam("createdBefore") Long createdBefore_ISO8601, @Nullable @BinderParam(BindListReposToPath.class) List<String> repos);
}
