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

package com.cdancy.artifactory.rest;

import java.io.Closeable;

import org.jclouds.rest.annotations.Delegate;

import com.cdancy.artifactory.rest.features.ArtifactApi;
import com.cdancy.artifactory.rest.features.RepositoryApi;
import com.cdancy.artifactory.rest.features.SearchApi;
import com.cdancy.artifactory.rest.features.StorageApi;
import com.cdancy.artifactory.rest.features.SystemApi;

public interface ArtifactoryApi extends Closeable {

   @Delegate
   ArtifactApi artifactApi();

   @Delegate
   RepositoryApi respositoryApi();

   @Delegate
   SearchApi searchApi();

   @Delegate
   StorageApi storageApi();

   @Delegate
   SystemApi systemApi();
}
