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

package com.github.artifactory.rest.domain.artifact;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Artifact {

   public abstract String uri();

   public abstract String downloadUri();

   public abstract String repo();

   public abstract String path();

   public abstract String created();

   public abstract String createdBy();

   public abstract String size();

   public abstract String mimeType();

   public abstract CheckSum checksums();

   public abstract CheckSum originalChecksums();

   Artifact() {
   }

   @SerializedNames({ "uri", "downloadUri", "repo", "path", "created", "createdBy", "size", "mimeType", "checksums",
         "originalChecksums" })
   public static Artifact create(String uri, String downloadUri, String repo, String path, String created,
         String createdBy, String size, String mimeType, CheckSum checksums, CheckSum originalChecksums) {
      return new AutoValue_Artifact(uri, downloadUri, repo, path, created, createdBy, size, mimeType, checksums,
            originalChecksums);
   }
}
