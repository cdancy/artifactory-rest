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

package com.cdancy.artifactory.rest.domain.search;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class SearchResult {

    @Nullable
    public abstract String uri();

    @Nullable
    public abstract String serverMd5();

    @Nullable
    public abstract String clientMd5();

    @Nullable
    public abstract String created();

    @Nullable
    public abstract String lastModified();

    @Nullable
    public abstract String lastDownloaded();

    @Nullable
    public abstract String license();

    @Nullable
    public abstract String found();

    @Nullable
    public abstract String status();

    @Nullable
    public abstract String version();

    public abstract boolean integration();

    SearchResult() {
    }

    @SerializedNames({ "uri", "serverMd5", "clientMd5", "created", "lastModified", "lastDownloaded", "license", "found", "status",
         "version", "integration" })
    public static SearchResult create(String uri, String serverMd5, String clientMd5, String created, String lastModified, String lastDownloaded,
                                      String license, String found, String status, String version, boolean integration) {
      return new AutoValue_SearchResult(uri, serverMd5, clientMd5, created, lastModified, lastDownloaded, license, found, status, version, integration);
    }
}
