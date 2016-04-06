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

package com.cdancy.artifactory.rest.options;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class PromoteBuildOptions {

   public abstract String status(); // defaults to 'promoted'

   public abstract String comment(); // default to 'error promoted'

   @Nullable
   public abstract String ciUser();

   @Nullable
   public abstract String timestamp(); // must be in format 'yyyy-MM-dd'T'HH:mm:ss.SSSZ'

   public abstract boolean dryRun();

   public abstract String sourceRepo();

   public abstract String targetRepo();

   public abstract boolean copy();

   public abstract boolean artifacts();

   public abstract boolean dependencies();

   @Nullable
   public abstract List<String> scopes();

   @Nullable
   public abstract Map<String, List<String>> properties();

   public abstract boolean failFast();

   PromoteBuildOptions() {
   }

   @SerializedNames({ "status", "comment", "ciUser", "timestamp", "dryRun", "sourceRepo", "targetRepo",
                        "copy", "artifacts", "dependencies", "scopes", "properties", "failFast" })
   public static PromoteBuildOptions create(String status, String comment, String ciUser, String timestamp,
                                            boolean dryRun, String sourceRepo, String targetRepo, boolean copy,
                                            boolean artifacts, boolean dependencies, List<String> scopes,
                                            Map<String, List<String>> properties, boolean failFast) {
      return new AutoValue_PromoteBuildOptions(status != null ? status : "promoted", comment != null ? comment : "error promoted", ciUser, timestamp,
                                       dryRun, sourceRepo, targetRepo, copy,
                                       artifacts, dependencies, scopes, properties, failFast);
   }
}
