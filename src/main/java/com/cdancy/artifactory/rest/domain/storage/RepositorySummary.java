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

package com.cdancy.artifactory.rest.domain.storage;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class RepositorySummary {

    public abstract String repoKey();

    public abstract String repoType();

    public abstract int foldersCount();

    public abstract int filesCount();

    public abstract String usedSpace();

    public abstract int itemsCount();

    @Nullable
    public abstract String packageType();

    @Nullable
    public abstract String percentage();

    RepositorySummary() {
    }

    @SerializedNames({ "repoKey", "repoType", "foldersCount", "filesCount", "usedSpace", "itemsCount", "packageType", "percentage" })
    public static RepositorySummary create(String repoKey, String repoType, int foldersCount, int filesCount, String usedSpace, int itemsCount,
                                           String packageType, String percentage) {
      return new AutoValue_RepositorySummary(repoKey, repoType, foldersCount, filesCount, usedSpace, itemsCount, packageType, percentage);
    }
}
