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

package com.github.artifactory.rest.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.rest.annotations.QueryParams;

@Consumes(MediaType.APPLICATION_JSON)
@Path("/api/storage")
public interface StoragApi {

   @Named("storage:set-item-properties")
   @Path("/{repoKey}/{itemPath}")
   @QueryParams(keys = { "recursive" }, values = { "1" })
   @PUT
   void setItemProperties(@PathParam("repoKey") String repoKey, @PathParam("itemPath") String itemPath,
         @QueryParam("properties") String commaSeparatedListOfKeyValuePairs);
}
